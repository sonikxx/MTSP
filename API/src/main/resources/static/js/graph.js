export class GraphDrawer {
    constructor(canvasId, isReadOnly = true, width = 700, height = 700) {
        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');

        this.canvas.width = width;
        this.canvas.height = height;
        this.points = [];
        this.distances = [];
        this.names = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        this.pointIndex = 0;
        this.routes = [];
        this.colors = [
            '#FFA500',
            '#FFD700',
            '#32CD32',
            '#FF69B4'
        ];
        this.mainColor = '#fd6535';

        this.canvas.style.cursor = 'not-allowed';
        if (isReadOnly === true) {
            this.canvas.addEventListener('click', this.addPoint.bind(this));
            this.canvas.style.cursor = 'crosshair';
        }

        this.setupHiDpiCanvas(width, height);
        this.drawGrid();
    }

    setupHiDpiCanvas(cssWidth, cssHeight) {
        const dpr = window.devicePixelRatio || 1;

        this.canvas.width = cssWidth * dpr;
        this.canvas.height = cssHeight * dpr;

        this.canvas.style.width = `${cssWidth}px`;
        this.canvas.style.height = `${cssHeight}px`;

        this.ctx.scale(dpr, dpr);
    }

    loadMap(data) {
        this.points = [];
        this.routes = [];
        this.distances = data.distances;

        data.cities.forEach(city => {
            const point = {
                name: city.name,
                x: city.x,
                y: city.y
            };
            this.points.push(point);
        });

        this.drawGrid();
        this.drawPoints();
    }

    getMapData() {
        return {
            cities: this.points.map(point => ({
                name: point.name,
                x: point.x,
                y: point.y
            })),
            distances: this.distances
        };
    }

    calculateDistance(pointA, pointB) {
        return Math.sqrt(Math.pow(pointB.x - pointA.x, 2) + Math.pow(pointB.y - pointA.y, 2));
    }

    updateDistances() {
        const numPoints = this.points.length;
        // Initialize a new distance matrix
        this.distances = Array.from({ length: numPoints }, () => Array(numPoints).fill(0));

        for (let i = 0; i < numPoints; i++) {
            for (let j = i + 1; j < numPoints; j++) {
                const distance = this.calculateDistance(this.points[i], this.points[j]);
                this.distances[i][j] = distance;
                this.distances[j][i] = distance;
            }
        }
    }

    drawGrid() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.ctx.beginPath();
        this.ctx.strokeStyle = "#ffffff";
        this.ctx.lineWidth = 2;
        for (let i = 0; i <= this.canvas.width; i += 50) {
            this.ctx.moveTo(i, 0);
            this.ctx.lineTo(i, this.canvas.height);
        }
        for (let i = 0; i <= this.canvas.height; i += 50) {
            this.ctx.moveTo(0, i);
            this.ctx.lineTo(this.canvas.width, i);
        }
        this.ctx.stroke();
    }

    addPoint(event) {
        if (this.pointIndex >= this.names.length) return;
        const rect = this.canvas.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;

        const name = this.names[this.pointIndex];
        this.points.push({ name, x, y });
        this.pointIndex++;

        this.updateDistances();

        this.drawPoints();
    }

    drawPoints() {
        this.drawGrid();

        this.drawRoutes();

        this.points.forEach((point, i) => {
            this.ctx.beginPath();
            this.ctx.arc(point.x, point.y, 5, 0, Math.PI * 2);
            if (i === 0) {
                this.ctx.fillStyle = this.mainColor;
            } else {
                this.ctx.fillStyle = "#60a5fa";
            }
            this.ctx.fill();


            const text = point.name;
            const textWidth = this.ctx.measureText(text).width;
            const canvasWidth = this.canvas.clientWidth;

            let textX;
            if (point.x + 8 + textWidth > canvasWidth) {
                textX = point.x - textWidth - 8;
            } else {
                textX = point.x + 8;
            }
            this.ctx.font = "bold 16px Arial";
            this.ctx.fillText(text, textX, point.y - 8);
        });
    }

    drawRoutes() {
        const depot = this.points[0];
        this.routes.forEach((route, index) => {
            const fullRoute = [depot, ...route, depot];
            this.ctx.strokeStyle = this.colors[index % this.colors.length];
            this.ctx.lineWidth = 2;

            this.ctx.beginPath();
            fullRoute.forEach((name, i) => {
                const point = this.points.find(p => p.name === name.name);
                if (point) {
                    if (i === 0) {
                        this.ctx.moveTo(point.x, point.y);
                    } else {
                        this.ctx.lineTo(point.x, point.y);
                    }
                } else {
                    console.log(`Point ${name.name} not found`);
                }
            });
            this.ctx.stroke();
        });
    }

    setRoutes(routes) {
        this.routes = routes;
        this.drawPoints();
    }

    clearRoutes() {
        this.routes = [];
        this.drawPoints();
    }

    clearAll() {
        this.points = [];
        this.routes = [];
        this.drawPoints();
    }
}
