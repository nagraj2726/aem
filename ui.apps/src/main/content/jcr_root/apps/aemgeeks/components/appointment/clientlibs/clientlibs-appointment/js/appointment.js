document.addEventListener('DOMContentLoaded', function() {
    const confirmBtn = document.querySelector('button');
    if (confirmBtn) {
        
        confirmBtn.addEventListener('click', () => {
            document.querySelector('.appointment-form').style.display = 'none';
            document.querySelector('.appointment-dates').style.display = 'block';
        });
    }
});
