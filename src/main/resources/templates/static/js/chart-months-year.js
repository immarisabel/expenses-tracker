    /* Retrieve the data from the server-side */
        var labels = /*[[${labels}]]*/ [];
        var credits = /*[[${credits}]]*/ [];
        var debits = /*[[${debits}]]*/ [];

        /* Create a map of month numbers to names */
        var monthNumberToNameMap = {
            "01": "JANUARY",
            "02": "FEBRUARY",
            "03": "MARCH",
            "04": "APRIL",
            "05": "MAY",
            "06": "JUNE",
            "07": "JULY",
            "08": "AUGUST",
            "09": "SEPTEMBER",
            "10": "OCTOBER",
            "11": "NOVEMBER",
            "12": "DECEMBER"
        };

        /* Create a new labels array for display */
        var displayLabels = labels.map(label => {
            var monthNumber = label.slice(0, 2); // Get the month number
            var year = label.slice(2); // Get the year
            var monthName = monthNumberToNameMap[monthNumber];
            return `${monthName} ${year}`;
        });

        /* Calculate the total income */
        var totalIncome = credits.reduce((a, b) => a + b, 0);

        /* Create the chart */
        var ctx = document.getElementById('chart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: displayLabels,
                datasets: [{
                    label: 'Debits',
                    data: debits,
                    backgroundColor: 'rgba(244, 164, 164, 0.5)', // #f4a4a4 at 50% opacity
                    borderColor: 'rgb(244, 164, 164)', // #f4a4a4
                    borderWidth: 1,
                    stack: 'stack1' // Assign the same stack ID to both datasets
                }, {
                    label: 'Credits',
                    data: credits,
                    backgroundColor: 'rgba(100, 116, 229, 0.5)', // #6474e5 at 50% opacity
                    borderColor: 'rgb(100, 116, 229)', // #6474e5
                    borderWidth: 1,
                    stack: 'stack1' // Assign the same stack ID to both datasets
                }]
            },
            options: {
                responsive: true,
                scales: {
                    x: {
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        beginAtZero: true,
                        stacked: true,
                        ticks: {
                            callback: function(value) {
                                return new Intl.NumberFormat('en-US', {
                                    style: 'currency',
                                    currency: 'USD'
                                }).format(value);
                            },
                            max: totalIncome // Set the maximum value to the total income
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                var label = context.dataset.label || '';

                                if (label) {
                                    label += ': ';
                                }

                                if (context.parsed !== null) {
                                    var total = context.chart.data.datasets.reduce(function(acc, dataset) {
                                        return acc + dataset.data[context.dataIndex];
                                    }, 0);
                                    var value = context.dataset.data[context.dataIndex];
                                    var percentage = ((value / total) * 100).toFixed(2) + '%';
                                    label += new Intl.NumberFormat('en-US', {
                                        style: 'currency',
                                        currency: 'USD'
                                    }).format(value) + ' (' + percentage + ')';
                                }

                                return label;
                            }
                        }
                    }
                },
                onClick: function(event, array) {
                    if (array.length > 0) {
                        var label = labels[array[0].index];
                        window.location.href = '/chart/' + label;
                    }
                }
            }
        });