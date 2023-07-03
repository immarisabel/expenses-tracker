function submitAllocations() {
    // Get the current month and year from the URL
    const urlParts = window.location.pathname.split('/');
    const monthYear = urlParts[urlParts.length - 1];

    // Create an array to store all the savings entities
    let savingsEntities = [];

    // Get all the manual input fields
    const inputFields = document.getElementsByClassName('manual-input');
    for (let i = 0; i < inputFields.length; i++) {
        // Get the current input field
        let inputField = inputFields[i];

        // Get the associated goal
        let goalId = inputField.getAttribute('data-goal-id');

        // Create a savings entity for this input field
        let savingsEntity = {
            goalId: goalId,
            amount: inputField.value
        };

        // Add the savings entity to the array
        savingsEntities.push(savingsEntity);
    }

    // Convert the array to JSON
    let jsonData = JSON.stringify(savingsEntities);

    // Create the URL with the formatted month and year
    const url = `/savings/allocate-savings/${encodeURIComponent(monthYear)}`;

    // Send the data to the server-side
    fetch(url, {
        method: 'POST',
        body: jsonData,
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then((response) => {
            if (response.ok) {
                alert('Allocation saved!');
            } else {
                alert('Failed to save allocation!');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}
