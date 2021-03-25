# Median Filter

### By Michael J. Alvarado


## Summary

This project consists of making a Client-Server application to process images with Median Filter
using Scala concurrency collections. The Client submit the image to two Servers, one will make
a Serial implementation, and the other will use Scala parallel collections. Both Servers will return
execution time and output images.

## Implementation

### SOFTWARE REQUIREMENTS

The code was done using Scala 2.13 with Visual Studio Code IDE with the Scala (Metals)
extension.

### MEDIAN FILTER

The idea behind the Median Filter is to take the neighbors pixels of the current pixel to then
make a median calculation of all RGB values in neighbors and set the current pixel to the
median. This process is done for each pixel of the image.

![pixels](https://user-images.githubusercontent.com/47261571/112519318-a17b5480-8d70-11eb-9387-6fb9520ed3a3.png)
**Figure 1. Concepts of iterating an image to filter.**


### SERVER/CLIENT

Once the Client is launched it will load a sample image showed in **Image 1**. Then the Client sends
this image to both Servers to process this image. The Clients wait for the result and print on
console execution times and save the output image on memory to verify the results of the
Median Filter.

_Serial Server_

The Serial Server makes the Median Filtering process in a single Thread. This means the image
will the process one pixel at a time.

_Parallel Server_

The Parallel Server makes the Median Filtering process in a Multi-Threading way. This way
multiple pixels will be process at the same time which in theory should lead to a faster images
filtering process.

## Outcome

The outcome test was done using the **Image 1** (256 x 256 pixels) which was sent by the client to both servers and got the filtered images shown in **Image 2 & 3**.

### EXECUTION TIME

The execution time is affected by the size of the image and computational power, but the
results should not the affected.

In **Figure 2** and **Figure 3** it can be seen the execution time in Serial and Parallel Servers, which
results as expected in theory. The Parallel Server results to be faster for the fact that it can
calculate multiple pixels at the same time which ultimately results in around half the execution
time of the Serial Server.

![image](https://user-images.githubusercontent.com/47261571/112519453-c53e9a80-8d70-11eb-864a-3ca63f810eed.png)
**Figure 2. Execution Time Image 1**

![image](https://user-images.githubusercontent.com/47261571/112519587-e56e5980-8d70-11eb-91ad-6b2b48f19158.png)
**Figure 3. Execution Time Image 4**


### IMAGE FILTERING

![image](https://user-images.githubusercontent.com/47261571/112519660-f7e89300-8d70-11eb-8c58-0a5ee8a5a88a.png)
```
Image 1. Original Input Image (Client).
```
![image](https://user-images.githubusercontent.com/47261571/112519693-fe770a80-8d70-11eb-811e-ac0da3adcf1a.png)
```
Image 2. Filtered Images (Serial Server).
```
![image](https://user-images.githubusercontent.com/47261571/112519693-fe770a80-8d70-11eb-811e-ac0da3adcf1a.png)
```
Image 3. Filtered Images (Parallel Server).
```
```
In Image 1 is the original
image given by the Client to
the Serves to filter, this image
has some Salt-and-Pepper
Noise.
```
```
In Image 2 we have the result
of the Serial Server where it
can be seen it Erased the Salt-
and-Pepper Noise given by
the original image.
```
```
In Image 3 we have the
Parallel Server results which
are the same as the Serial
Server since it uses the same
Median Filtering Algorithm,
but much faster than the
Serial Server.
```
