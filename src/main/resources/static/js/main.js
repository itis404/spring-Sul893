function votePost(button, value) {
    var postContainer = button.closest('.post-votes');
    var postId = postContainer.getAttribute('data-post-id');
    if (!postId) return;

    fetch('/api/posts/' + postId + '/vote?value=' + value, {
        method: 'POST'
    }).then(function(response) {
        if (response.ok) return response.json();
        else throw response;
    }).then(function(newCount) {
        var countElement = postContainer.querySelector('.vote-count');
        if (countElement) countElement.textContent = newCount;
    }).catch(function(err) {
        console.error('Vote error:', err);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    var voteButtons = document.querySelectorAll('.vote-btn');
    voteButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            var value = btn.classList.contains('vote-up') ? 1 : -1;
            votePost(btn, value);
        });
    });

    var slugInput = document.getElementById('slug');
    if (slugInput) {
        var nameInput = document.getElementById('name');
        if (nameInput) {
            nameInput.addEventListener('input', function() {
                if (!slugInput.getAttribute('data-manual')) {
                    slugInput.value = nameInput.value
                        .toLowerCase()
                        .replace(/[^a-z0-9]+/g, '-')
                        .replace(/^-|-$/g, '');
                }
            });
        }
        slugInput.addEventListener('input', function() {
            slugInput.setAttribute('data-manual', 'true');
        });
    }
});