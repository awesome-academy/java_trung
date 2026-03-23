(function () {
    'use strict';

    const STATUS_COLORS = {
        PENDING:   '#ffc107',
        CONFIRMED: '#17a2b8',
        PREPARING: '#6f42c1',
        DELIVERED: '#0d6efd',
        DONE:      '#198754',
        CANCELLED: '#dc3545'
    };

    function statusColor(label) {
        return STATUS_COLORS[label] || '#6c757d';
    }

    function show(el) { el.style.removeProperty('display'); }
    function hide(el) { el.style.setProperty('display', 'none', 'important'); }

    function renderRevenueChart(revenueByMonth) {
        new Chart(document.getElementById('revenueChart'), {
            type: 'line',
            data: {
                labels: revenueByMonth.labels,
                datasets: [{
                    label: 'Revenue (VND)',
                    data:  revenueByMonth.data,
                    borderColor: '#0d6efd',
                    backgroundColor: 'rgba(13,110,253,0.08)',
                    pointBackgroundColor: '#0d6efd',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: v => new Intl.NumberFormat('vi-VN').format(v)
                        }
                    }
                }
            }
        });
    }

    function renderStatusChart(ordersByStatus) {
        const labels = ordersByStatus.labels;
        new Chart(document.getElementById('statusChart'), {
            type: 'doughnut',
            data: {
                labels,
                datasets: [{
                    data:            ordersByStatus.data,
                    backgroundColor: labels.map(statusColor),
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom', labels: { padding: 12 } }
                }
            }
        });
    }

    function renderTopProductsChart(topProducts) {
        new Chart(document.getElementById('topProductsChart'), {
            type: 'bar',
            data: {
                labels: topProducts.labels,
                datasets: [{
                    label: 'Units Sold',
                    data:  topProducts.data,
                    backgroundColor: [
                        'rgba(25,135,84,0.75)',
                        'rgba(13,110,253,0.75)',
                        'rgba(255,193,7,0.75)',
                        'rgba(111,66,193,0.75)',
                        'rgba(220,53,69,0.75)'
                    ],
                    borderRadius: 4
                }]
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                plugins: { legend: { display: false } },
                scales: { x: { beginAtZero: true, ticks: { stepSize: 1 } } }
            }
        });
    }

    async function loadCharts() {
        const loading   = document.getElementById('chartsLoading');
        const container = document.getElementById('chartsContainer');
        const errorBox  = document.getElementById('chartsError');

        const statsUrl = document.querySelector('meta[name="dashboard-stats-url"]')?.content;

        try {
            const res = await fetch(statsUrl);
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const stats = await res.json();

            renderRevenueChart(stats.revenueByMonth);
            renderStatusChart(stats.ordersByStatus);
            renderTopProductsChart(stats.topProducts);

            hide(loading);
            show(container);
        } catch (err) {
            console.error('Dashboard chart error:', err);
            hide(loading);
            show(errorBox);
        }
    }

    document.addEventListener('DOMContentLoaded', loadCharts);
})();
