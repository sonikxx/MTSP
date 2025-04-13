import {GraphDrawer} from './graph.js';

class CreatePageController {
    constructor(fileInputId, saveButtonId, canvasId) {
        this.fileInput = document.getElementById(fileInputId);
        this.saveButton = document.getElementById(saveButtonId);

        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');

        const isReadOnlyGraph = !document.location.href.includes('main');
        this.graphDrawer = new GraphDrawer(canvasId, isReadOnlyGraph);
    }

    init() {
        this.fileInput.addEventListener('change', (e) => this.loadFile(e));
        this.saveButton.addEventListener('click', () => this.saveMap());
    }

    loadFile(e) {
        const file = e.target.files[0];
        const reader = new FileReader();

        reader.onload = (e) => {
            const data = JSON.parse(e.target.result);
            this.graphDrawer.loadMap(data);
        };

        reader.readAsText(file);
    }

    saveMap() {
        const map = this.graphDrawer.getMapData();
        map.name = "123"
        console.log(map);
        fetch('/protected/v1/save/map', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(map)
        })
        .then(response => {
            if (response.ok) {
                alert(response);
            } else {
                alert("Failed to save the map.");
            }
        })
        .catch(error => {
            console.log("Save error:", error);
            alert("An error occurred while cancelling.");
        });
    }
}

const createPageController = new CreatePageController('fileInput', 'solveMTSPButton', 'map');
createPageController.init();