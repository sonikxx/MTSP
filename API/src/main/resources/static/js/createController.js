import {GraphDrawer} from './graph.js';

class CreatePageController {
    constructor(fileInputId, saveButtonId, canvasId) {
        this.fileInput = document.getElementById(fileInputId);
        this.saveButton = document.getElementById(saveButtonId);
        this.nameInput = document.getElementById('mapName');
        this.allMapsContainer = document.getElementById('allMapsContainer');

        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');

        const isReadOnlyGraph = !document.location.href.includes('main');
        this.graphDrawer = new GraphDrawer(canvasId, isReadOnlyGraph);

        this.mapName = '';
        this.isPublic = false;
    }

    init() {
        this.fileInput.addEventListener('change', (e) => this.loadFile(e));
        this.saveButton.addEventListener('click', () => this.saveMap());

        this.nameInput.addEventListener('change', () => this.mapName = this.nameInput.value);
        document.querySelectorAll('input[name="visibility"]').forEach((radio) => {
            radio.addEventListener('change', () => {
                console.log(this);
                this.isPublic = (document.querySelector('input[name="visibility"]:checked').id === 'public');
            });
        });


        this.loadUserInfo();
        this.loadAvailableMaps();
    }

    loadUserInfo() {
        fetch('/protected/v1/info')
            .then(response => response.json())
            .then(data => {
                document.getElementById('userName').textContent = data.userName;
            })
            .catch(err => console.log(err));
    }

    loadAvailableMaps() {

        fetch('/protected/v1/maps')
            .then(response => response.json())
            .then(data => {

                const template = document.getElementById('map-card-template');

                this.allMapsContainer.innerHTML = ''; // Clear existing cards
                let cnt = 0;
                data.forEach(map => {
                    const clone = template.content.cloneNode(true);
                    const card = clone.querySelector('.card');
                    card.href = `/main/${map.id}`;
                    card.querySelector('.card-title').textContent = map.name;
                    card.querySelector('.card-author').textContent = `Author: ${map.ownerName}`;
                    card.querySelector('.card-data').textContent = `Creation date: ${map.creationDate}`;
                    this.allMapsContainer.appendChild(clone);
                    cnt++;
                });
                if (cnt !== 0) {
                    document.getElementById('historyContainer').style.display = 'flex';
                }
            })
            .catch(err => {
                console.error('Failed to load maps:', err);
            });
    }

    loadFile(e) {

        this.graphDrawer.clearAll();
        const file = e.target.files[0];

        const maxSizeInBytes = 1 * 1024 * 1024; // 1 MB

        if (file.size > maxSizeInBytes) {
            alert("Файл слишком большой. Максимальный размер — 1 МБ.");
            this.fileInput.value = "";
            return;
        }

        const reader = new FileReader();

        reader.onload = (e) => {
            const data = JSON.parse(e.target.result);
            this.graphDrawer.loadMap(data);
        };

        reader.readAsText(file);
    }

    validateMap() {
        if (this.graphDrawer.points.length < 3) {
            alert('Map must contain at least 3 points. Please click on the map to add points or select a file to upload.');
            return false;
        }
        if (this.mapName === '') {
            alert('Please enter map name.');
            return false;
        }
        return true;
    }

    saveMap() {
        if (!this.validateMap()) return;

        const map = this.graphDrawer.getMapData();
        map.name = this.mapName;
        map.isPublic = this.isPublic;
        fetch('/protected/v1/save/map', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(map)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error solving MTSP.");
            }
            return response.json();
        }).then(response => {
             window.location.href = `/main/${response.mapId}`;
        })
        .catch(error => {
            console.log("Save error:", error);
            alert("An error occurred while cancelling.");
        });
    }
}

const createPageController = new CreatePageController('fileInput', 'solveMTSPButton', 'map');
createPageController.init();