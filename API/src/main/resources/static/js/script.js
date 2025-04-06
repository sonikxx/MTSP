document.addEventListener('DOMContentLoaded', function() {
    console.log("Page loaded and JavaScript is running!");
});


function startPoling(requestId, onIntermediate, onSuccess, checkInterval = 300, maxAttempts = 100) {
    console.log("Start poling for request " + requestId);
    const pollingKey = `polling-${requestId}`;
    let attempts = parseInt(localStorage.getItem(pollingKey), 10) || 0;

    async function poll() {
        try {
            const response = await fetch(`/protected/v1/result/${requestId}`);
            const data = await response.json();
            if (data.status === "SOLVED") {
                console.log("Solution found!");
                clearInterval(pollingInterval);
                localStorage.removeItem(pollingKey);
                onSuccess(data);
            } else if (data.status === "INTERMEDIATE") {
                console.log("Still working...");
                onIntermediate(data);
            } else if (data.status === "FAILED") {
                throw new Error("Internal error occurred.");
            }
        } catch (error) {
            console.log("Error while polling: " + error);
            clearInterval(pollingInterval);
            localStorage.removeItem(pollingKey);
        }
    }

    const pollingInterval = setInterval(() => {
        if (attempts > maxAttempts) {
            console.log("Max attempts exceeded");
            clearInterval(pollingInterval);
            localStorage.removeItem(pollingKey);
            return;
        }
        attempts++;
        localStorage.setItem(pollingKey, attempts.toString());
        poll();
    }, checkInterval);
}
