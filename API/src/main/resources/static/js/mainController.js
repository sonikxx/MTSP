import {GraphDrawer} from './graph.js';


export class MtspPageController {
    constructor(solveButtonId, canvasId) {
        this.solveButton = document.getElementById(solveButtonId);
        this.canselButton = document.getElementById('canselButton');
        this.downloadResultButton = document.getElementById('downloadResultButton');
        this.salesmanNumberInput = document.getElementById('salesmanCount');
        this.algorithmSelect = document.getElementById('tspAlgorithm');

        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');

        const isReadOnlyGraph = !document.location.href.includes('main');
        this.graphDrawer = new GraphDrawer(canvasId, isReadOnlyGraph);

        this.requestId = null;

        this.salesmanNumber = this.salesmanNumberInput.value;
        this.algorithm = this.algorithmSelect.value;
        this.algorithmParams = {
            maxIterations: 1000,
        }
        this.result = null;

        this.activePolling = null;
        this.pollingInterval = 300;
        this.pollingMaxRetries = 100;

        this.name = null;
        this.mapId = null;
    }

    init() {
        const pathParts = window.location.pathname.split('/');
        if (pathParts.length < 3) {
            alert("Please login first.");
            return;
        }
        this.mapId = pathParts[2];

        this.solveButton.addEventListener('click', (e) => this.solve(e));
        this.downloadResultButton.addEventListener('click', (e) => this.downloadResult(e));
        this.canselButton.addEventListener('click', (e) => this.cancelRequest(e));

        this.salesmanNumberInput.addEventListener('change', (e) => this.salesmanNumber = e.target.value);
        this.algorithmSelect.addEventListener('change', (e) => this.algorithm = e.target.value);

        this.loadMap();
        this.loadAvailableMaps();
    }

    switchToSolvingMode() {
        this.graphDrawer.clearRoutes();


        this.solveButton.disabled = true;
        this.solveButton.innerText = 'Solving...';

        this.downloadResultButton.disabled = true;
    }

    switchToReadyMode() {
        this.solveButton.disabled = false;
        this.solveButton.innerText = 'Solve';

        this.downloadResultButton.disabled = false;
    }

    loadMap() {
        fetch(`/protected/v1/map/${this.mapId}`)
        .then(response => response.json())
        .then(data => {
            this.graphDrawer.loadMap(data);
            this.name = data.name;
            document.getElementById('userName').textContent = data.ownerName;
            console.log(this.name);
        })
        .catch(err => {
            alert("Couldn't load map.");
            document.location.href = '/create';
        });
    }

    loadAvailableMaps() {
        fetch(`/protected/v1/maps`)
       .then(response => response.json())
       .then(data => {
           console.log(data);
       })
        .catch(err => {
            console.log(err);
        })
    }

    validateUserInputs() {
        if (this.salesmanNumber < 2) {
            alert('Salesman number must be greater than 1');
            return false;
        }
        if (this.graphDrawer.points.length < this.salesmanNumber) {
            alert('Salesman number must be less than or equal to number of points');
            return false;
        }
        return true;
    }


    solve(e) {
        if (!this.validateUserInputs()) {
            return;
        }

        this.switchToSolvingMode();
        fetch('/protected/v1/solve', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                mapId: this.mapId,
                salesmanNumber: this.salesmanNumber,
                algorithm: this.algorithm,
                algorithmParams: this.algorithmParams
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error solving MTSP.");
                }
                return response.json();
            })
            .then(result => {
                this.requestId = result.requestId;
                console.log("requestId is: " + this.requestId);

                this.startPoling(result.requestId,
                    data => {
                        console.log("Receiving solution:", data);
                        this.graphDrawer.setRoutes(data.routes);
                        this.result = data;
                    },
                    data => {
                        console.log("Received solution:", data);
                        this.graphDrawer.setRoutes(data.routes);
                        this.result = data;
                        this.switchToReadyMode();
                    }
                );
            })
            .catch(error => {
                this.switchToReadyMode();
                alert(error.message);
            });
    }

    getResult() {
        return {
            result : this.result,
            salesmanNumber : this.salesmanNumber,
            algorithm : this.algorithm,
            algorithmParams : this.algorithmParams,
        }
    }

    downloadResult(e) {
        if (this.result === null) {
            alert("You have to solve task before downloading result.");
            return;
        } else if (this.result.status !== 'SOLVED') {
            alert("You have to wait until the result is ready.");
            return;
        }

        const data = this.getResult();

        const blob = new Blob([JSON.stringify(data, null, 4)], { type: "application/json" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `mtsp-result-${this.requestId}.json`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }

    startPoling(requestId, onIntermediate, onSuccess) {
        console.log("Start poling for request " + requestId);
        const pollingKey = `polling-${requestId}`;
        let attempts = parseInt(localStorage.getItem(pollingKey), 10) || 0;

        const poll = async () => {
            try {
                const response = await fetch(`/protected/v1/result/${requestId}`);
                const data = await response.json();
                if (data.status === "SOLVED") {
                    console.log("Solution found!");
                    this.stopPolling(requestId);
                    onSuccess(data);
                } else if (data.status === "INTERMEDIATE") {
                    console.log("Still working...");
                    onIntermediate(data);
                } else if (data.status === "FAILED") {
                    throw new Error("Internal error occurred.");
                }
            } catch (error) {
                console.log("Error while polling: " + error);
                this.stopPolling(requestId);
            }
        }

        this.activePolling = setInterval(() => {
            if (attempts > this.pollingMaxRetries) {
                console.log("Max attempts exceeded");
                this.stopPolling(requestId);
                return;
            }
            attempts++;
            localStorage.setItem(pollingKey, attempts.toString());
            poll();
        }, this.pollingInterval);
    }

    stopPolling(requestId) {
        const pollingKey = `polling-${requestId}`;
        const intervalId = this.activePolling;
        if (intervalId) {
            clearInterval(intervalId);
            this.activePolling = null;
            localStorage.removeItem(pollingKey);
            console.log(`Stopped polling for request ${requestId}`);
        }
    }

    cancelRequest(e) {
        if (!this.requestId) {
            alert("You have to start solving task before cancelling it.");
            return;
        }

        fetch(`/protected/v1/solve/${this.requestId}`, {
            method: "DELETE"
        })
        .then(response => {
            if (response.ok) {
                this.stopPolling(this.requestId);
                this.switchToReadyMode();
                alert("Request cancelled.");
            } else {
                this.switchToReadyMode();
                alert("Failed to cancel the request.");
            }
        })
        .catch(error => {
            this.switchToReadyMode();
            console.log("Cancel error:", error);
            alert("An error occurred while cancelling.");
        });
    }
}


const controller = new MtspPageController('solveMTSPButton', 'map');
controller.init();