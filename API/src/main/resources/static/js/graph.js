class GraphDrawer {
    constructor(canvasId, width = 500, height = 500) {
        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');
        this.canvas.width = width;
        this.canvas.height = height;
        this.points = [];
        this.names = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        this.pointIndex = 0;
        this.routes = [];
        this.colors = [
            '#666666', // Темно-серый
            '#3b6e3a', // Темно-зеленый
            '#b0b0b0', // Теплый серый
            '#7a4b3c', // Темно-коричневый
            '#4f6d7a'  // Серый с голубым оттенком
        ];

        this.canvas.addEventListener('click', this.addPoint.bind(this));
        this.drawGrid();
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
        this.drawPoints();
    }

    drawPoints() {
        this.drawGrid();

        this.drawRoutes();

        this.points.forEach(point => {
            this.ctx.beginPath();
            this.ctx.arc(point.x, point.y, 5, 0, Math.PI * 2);
            this.ctx.fillStyle = "#60a5fa";
            this.ctx.fill();
            this.ctx.fillText(point.name, point.x + 8, point.y - 8);
        });
    }

    drawRoutes() {
        this.routes.forEach((route, index) => {
            this.ctx.strokeStyle = this.colors[index % this.colors.length];
            this.ctx.lineWidth = 2;

            this.ctx.beginPath();
            route.forEach((name, i) => {
                const point = this.points.find(p => p.name === name);
                if (point) {
                    if (i === 0) {
                        this.ctx.moveTo(point.x, point.y);
                    } else {
                        this.ctx.lineTo(point.x, point.y);
                    }
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
}
