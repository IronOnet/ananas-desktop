---
id: running
title: Running
---

![Running job in Ananas Analytics](assets/running.png)

## Running a data flow

When you have [tested](testing.md) the steps of your data flow, you can click on the `run` button to initiate a run at any time. You must click the button of the last step of your data flow (a destination or a visualization step). During a run, all of your sources will fetch fresh data and then send it through your steps. Eventually, your destinations will export the data.

Any job execution state is posted in **real time** in the Job History table in the left sidebar.

If a run succeeds, you should see its state `DONE`. 

If a run failed, you should see its state `FAILED` with the last message error.

## What are the runnable steps? 

All destination and visualizaton steps are runnable. Indeed the transform step can only be tested because their output data is not exported to a physical data store. You will need to add a destination step to explore its ouput. 
