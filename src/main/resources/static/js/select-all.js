 document.addEventListener('DOMContentLoaded', (event) => {

    // Function to toggle the selection of all checkboxes
    function toggleSelectAll() {
      const checkboxes = document.querySelectorAll('input[name="selectedTransactionsIds"]');
      const selectAllCheckbox = document.getElementById('selectAll');

      checkboxes.forEach((checkbox) => {
        checkbox.checked = selectAllCheckbox.checked;
      });
    }

    // Add event listener to the "Select All" checkbox
    const selectAllCheckbox = document.getElementById('selectAll');
    selectAllCheckbox.addEventListener('change', toggleSelectAll);


// For the form
document.getElementById('updateCategoryForm').addEventListener('submit', function(e) {
    e.preventDefault();
    var checkedBoxes = document.querySelectorAll('input[name="selectedTransactionsIds"]:checked');
    var formData = new FormData();
    checkedBoxes.forEach(function(box) {
        formData.append('selectedTransactionsIds', box.value);
    });
    // Add other form data if necessary
    formData.append('categoryId', this.querySelector('select[name="categoryId"]').value);
    fetch(this.action, {
        method: this.method,
        body: formData
    }).then(function(response) {
        if (!response.ok) {
            console.error('Failed to update category', response);
        } else {
            // Optionally reload the page or do something else on success
            location.reload();
        }
    });
});

});