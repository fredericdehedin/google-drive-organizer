function archiveProgress() {
    return {
        isOpen: false,
        steps: [],
        eventSource: null,

        get isTerminal() {
            const last = this.steps[this.steps.length - 1];
            return last && (last.status === 'done' || last.status === 'failed');
        },

        closeOverlay() {
            this.isOpen = false;
            this.steps = [];
        },

        startArchive(fileId, fileName) {
            this.steps = [];
            this.isOpen = true;
            this.eventSource = new EventSource('/api/files/' + fileId + '/archive/progress');

            this.eventSource.addEventListener('progress', (e) => {
                const data = JSON.parse(e.data);
                const prev = this.steps[this.steps.length - 1];
                if (prev && prev.status === 'running') {
                    prev.status = data.step === 'FAILED' ? 'failed' : 'done';
                }
                if (data.step !== 'DONE') {
                    this.steps.push({ step: data.step, message: data.message, status: 'running' });
                }
                if (data.step === 'DONE' || data.step === 'FAILED') {
                    this.eventSource.close();
                    if (data.step === 'DONE') setTimeout(() => this.closeOverlay(), 2000);
                }
            });

            this.eventSource.onopen = () => {
                fetch('/api/files/' + fileId + '/archive?fileName=' + encodeURIComponent(fileName))
                    .then(response => {
                        if (!response.ok) this._handleError('Archive request failed');
                    })
                    .catch(() => this._handleError('Network error'));
            };

            this.eventSource.onerror = () => {
                if (!this.isTerminal) this._handleError('Connection error');
            };
        },

        _handleError(msg) {
            const prev = this.steps[this.steps.length - 1];
            if (prev && prev.status === 'running') prev.status = 'failed';
            this.steps.push({ step: 'FAILED', message: msg, status: 'failed' });
            if (this.eventSource) this.eventSource.close();
        }
    };
}
