import { MtspPageController } from './controller.js';

const controller = new MtspPageController('fileInput', 'solveMTSPButton', 'map');
controller.init()

document.addEventListener('DOMContentLoaded', function() {
    console.log("Page loaded and JavaScript is running!");
});




