function moveFile(fileId, folderName) {
    fetch('/api/files/' + fileId + '/move?folderName=' + encodeURIComponent(folderName))
        .catch(() => console.error('Move request failed'));
}
