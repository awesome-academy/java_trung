/**
 * Categories list page — CSV import logic.
 *
 * i18n messages are injected by Thymeleaf as data-* attributes on
 * the hidden #csvConfig element and read once at DOMContentLoaded.
 */
(function () {
    'use strict';

    const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    let _csv = {};

    /** Simple positional placeholder formatter: _fmt('Hello {0}!', 'world') → 'Hello world!' */
    function _fmt(template, ...args) {
        return args.reduce((s, v, i) => s.replace('{' + i + '}', v), template);
    }

    // ── Bootstrap modal reset ─────────────────────────────────────────────────
    function initModalReset() {
        document.getElementById('importModal')
            .addEventListener('hidden.bs.modal', () => {
                document.getElementById('importFile').value = '';
                hideImportAlert();
                document.getElementById('downloadErrorBtn').classList.add('d-none');
                setImportLoading(false);
            });
    }

    // ── Main import handler ───────────────────────────────────────────────────
    async function submitCsvImport() {
        const fileInput = document.getElementById('importFile');

        // Guard clauses — fail fast on client side
        if (!fileInput.files || fileInput.files.length === 0) {
            showImportAlert('warning', _csv.noFile);
            return;
        }
        const file = fileInput.files[0];
        if (file.size > MAX_FILE_SIZE) {
            showImportAlert('danger', _fmt(_csv.tooLarge, (file.size / 1024 / 1024).toFixed(2)));
            return;
        }

        setImportLoading(true);
        hideImportAlert();
        document.getElementById('downloadErrorBtn').classList.add('d-none');

        try {
            const form = document.getElementById('importForm');
            const response = await fetch(form.action, {
                method: 'POST',
                body: new FormData(form)
            });

            const contentType = response.headers.get('Content-Type') || '';

            // ── Server trả binary CSV (có lỗi dữ liệu) ───────────────────────
            if (contentType.includes('text/csv')) {
                const blob = await response.blob();

                showImportAlert('danger',
                    '<i class="bi bi-exclamation-triangle me-1"></i>' + _csv.errorRows);

                const downloadBtn = document.getElementById('downloadErrorBtn');
                downloadBtn.classList.remove('d-none');
                downloadBtn.onclick = () => {
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = _csv.errorFile;
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    URL.revokeObjectURL(url);
                };
                return;
            }

            // ── Server trả JSON (success hoặc lỗi nghiệp vụ) ─────────────────
            if (!contentType.includes('application/json')) {
                showImportAlert('danger', _fmt(_csv.errorHttp, response.status));
                return;
            }

            const data = await response.json();

            if (data.success) {
                showImportAlert('success',
                    '<i class="bi bi-check-circle me-1"></i>' + _fmt(_csv.success, data.count));
                setTimeout(() => location.reload(), 1800);
            } else {
                showImportAlert('danger', data.message || _fmt(_csv.errorHttp, response.status));
            }

        } catch (err) {
            console.error('CSV import error', err);
            showImportAlert('danger', _csv.errorNetwork);
        } finally {
            setImportLoading(false);
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    function showImportAlert(type, html) {
        const el = document.getElementById('importAlert');
        el.className = `alert alert-${type}`;
        el.innerHTML = html;
        el.classList.remove('d-none');
    }

    function hideImportAlert() {
        const el = document.getElementById('importAlert');
        el.classList.add('d-none');
        el.innerHTML = '';
    }

    function setImportLoading(loading) {
        document.getElementById('importSubmitBtn').disabled = loading;
        document.getElementById('importSpinner').classList.toggle('d-none', !loading);
        document.getElementById('importIcon').classList.toggle('d-none', loading);
    }

    // ── Init ──────────────────────────────────────────────────────────────────
    document.addEventListener('DOMContentLoaded', () => {
        // Read i18n messages from data-* attributes set by Thymeleaf
        const cfg = document.getElementById('csvConfig').dataset;
        _csv = {
            noFile:       cfg.msgNoFile,
            tooLarge:     cfg.msgTooLarge,   // {0} = MB size
            success:      cfg.msgSuccess,    // {0} = saved count
            errorRows:    cfg.msgErrorRows,
            errorHttp:    cfg.msgErrorHttp,  // {0} = HTTP status code
            errorNetwork: cfg.msgErrorNetwork,
            errorFile:    cfg.msgErrorFile
        };

        initModalReset();

        // Expose submit handler globally so the onclick attribute in the template can call it
        window.submitCsvImport = submitCsvImport;
    });
}());
