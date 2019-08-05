---
id: visualization-barchart
title: Bar Chart
---

![ananas analytic bar chart](assets/visualization.png)


# Settings

- Dimensions

  The possible columns used as the X axis, only `String` (or compatible) type can be used as dimensions. Only 1 column can be selected

- Measures

  The numeric columns to be visualized, only numeric type could be selected. Multiple columns is possible, but they share the same scale.

- Chart Title

  The title of the chart

- X Axis Label

  The Label of X Axis

- Y Axis Label

  The Label of Y Axis

- Stacked Bar Chart? (*0.9.0+*)

	Display the bars in a stack way

- Horizontal Bar Chart? (*0.9.0+*)

	Display the bars in a horizontal way

# Advanced Settings

- SQL

  The SQL query to filter the result for visualization. Note: SQL query will not be used to calculate the processed data. It only filters the data for display. You can change the way your job result looks like without re-running the job.
