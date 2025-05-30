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
        this.algorithmParams = new Map();
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
        this.algorithmSelect.addEventListener('change', (e) => {
            this.algorithm = e.target.value;
            this.updateAlgorithmParams(e);
        });

        this.updateAlgorithmParams({ target: this.algorithmSelect});

        this.loadUserInfo();
        this.loadMap();
    }

    switchToSolvingMode() {
        this.graphDrawer.clearRoutes();


        this.solveButton.disabled = true;
        this.solveButton.innerText = 'Solving...';

        this.downloadResultButton.disabled = true;
        this.downloadResultButton.classList.remove('allowed-download');
        this.downloadResultButton.classList.add('disabled-download');
    }

    switchToReadyMode() {
        this.solveButton.disabled = false;
        this.solveButton.innerText = 'Solve';

        this.downloadResultButton.disabled = false;
        this.downloadResultButton.classList.remove('disabled-download');
        this.downloadResultButton.classList.add('allowed-download');
    }

    loadUserInfo() {
        fetch('/protected/v1/info')
            .then(response => response.json())
            .then(data => {
                document.getElementById('userName').textContent = data.userName;
            })
            .catch(err => console.log(err));
    }

    loadMap() {
        fetch(`/protected/v1/map/${this.mapId}`)
        .then(response => response.json())
        .then(data => {
            this.graphDrawer.loadMap(data);
            this.name = data.name;
            document.getElementById('mapTitleName').textContent = data.name;
            document.getElementById('mapAuthorName').textContent = data.ownerName;
            this.loadBestSolution();
            console.log(this.name);
        })
        .catch(err => {
            alert("Couldn't load map.");
            document.location.href = '/create';
        });
    }

    loadBestSolution() {
        // Fetch the best solution for the map if available
        fetch(`/protected/v1/best/${this.mapId}`)
            .then(response => response.json())
            .then(data => {
                if (data) {
                    console.log("Best solution found:", data);
                    this.graphDrawer.setRoutes(data.routes);
                    this.fillResultSection('best', {
                        totalCost: data.totalCost,
                        timeMs: data.totalTime,
                        algorithm: data.algorithm,
                        salesmanNumber: data.routes.length,
                    });
                    this.result = data;
                } else {
                    console.log("No solution found.");
                }
            })
            .catch(err => {
                alert("Couldn't load best solution:" + err);
            });
    }

    fillResultSection(prefix, { salesmanNumber, algorithm, totalCost, timeMs }) {
        let algorithmName = "unknown"
        if (algorithm === "bruteForce") {
            algorithmName = "Brute force"
        } else if (algorithm === "python") {
            algorithmName = "Simulated annealing (Python)"
        } else if (algorithm === "genetic") {
            algorithmName = "Genetic algorithm"
        }
        document.getElementById(`${prefix}SalesmanCount`).textContent = salesmanNumber;
        document.getElementById(`${prefix}Algorithm`).textContent = algorithmName;
        document.getElementById(`${prefix}TotalCost`).textContent = totalCost;
        document.getElementById(`${prefix}Time`).textContent = `${timeMs}ms`;
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
                algorithmParams: Object.fromEntries(this.algorithmParams)
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
                        this.fillResultSection('your', {
                            salesmanNumber: data.routes.length,
                            algorithm: this.algorithm,
                            totalCost: data.totalCost,
                            timeMs: data.totalTime,
                        });
                    },
                    data => {
                        console.log("Received solution:", data);
                        this.graphDrawer.setRoutes(data.routes);
                        this.result = data;
                        this.fillResultSection('your', {
                            salesmanNumber: data.routes.length,
                            algorithm: this.algorithm,
                            totalCost: data.totalCost,
                            timeMs: data.totalTime,
                        });
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
        delete this.result.algorithm;
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
            } else {
                this.stopPolling(this.requestId);
                this.switchToReadyMode();
                alert("Failed to cancel the request.");
            }
        })
        .catch(error => {
            this.switchToReadyMode();
            this.stopPolling(this.requestId);
            console.log("Cancel error:", error);
            alert("An error occurred while cancelling.");
        });
    }

    updateAlgorithmParams(e) {
        const paramsConfig = {
            bruteForce: {
                maxIterations: { label: "Max Iterations", type: "number", min: -1, value: -1 }
            },
            genetic: {
                populationSize: { label: "Population Size", type: "number", min: 2, max: 500, value: 300 },
                generations: { label: "Generations", type: "number", min: 1, max: 4000, value: 1000 },
                mutationRate: { label: "Mutation Rate", type: "number", step: 0.01, min: 0.01, max: 1.0, value: 0.05 }
            },
            python: {
                distance_weight: { label: "Distance Weight", type: "number", min: 1, max: 40, value: 10 },
                balance_weight: { label: "Balance Weight", type: "number", min: 1, max: 10, value: 5 }
            }
        };

        const selectedAlgorithm = e.target.value;
        this.algorithmParams.clear(); // Reset stored params for new algorithm

        const container = document.getElementById("algorithmParams");
        container.innerHTML = ""; // Clear old fields

        const params = paramsConfig[selectedAlgorithm];
        for (const [key, config] of Object.entries(params)) {
            const wrapper = document.createElement("div");
            wrapper.className = "input-field";

            const label = document.createElement("label");
            label.className = "input-label";
            label.htmlFor = key;
            label.innerText = config.label;

            const input = document.createElement("input");
            input.type = config.type;
            input.id = key;
            input.name = key;
            input.value = config.value ?? "";
            if (config.min !== undefined) input.min = config.min;
            if (config.max !== undefined) input.max = config.max;
            if (config.step !== undefined) input.step = config.step;
            input.className = "input-number";

            // Initial store
            this.algorithmParams.set(key, input.value);

            // Update on input change
            input.addEventListener("input", () => {
                this.algorithmParams.set(key, input.value);
                console.log(`Updated param: ${key} = ${input.value}`);
            });

            wrapper.appendChild(label);
            wrapper.appendChild(input);
            container.appendChild(wrapper);
        }

        console.log(this.algorithmParams);
    }

}


const controller = new MtspPageController('solveMTSPButton', 'map');
controller.init();