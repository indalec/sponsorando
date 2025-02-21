    document.addEventListener('DOMContentLoaded', function() {
        const teamContainer = document.querySelector('.row.row-cols-1');
        const members = Array.from(teamContainer.children);

        // Fisher-Yates shuffle algorithm
        for (let i = members.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [members[i], members[j]] = [members[j], members[i]];
        }

        // Clear and re-append shuffled members
        while (teamContainer.firstChild) {
            teamContainer.removeChild(teamContainer.firstChild);
        }
        members.forEach(member => teamContainer.appendChild(member));
    });