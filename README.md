# Diffusion-limited-aggregationFX
This generates Diffusion-limited-aggregation using the JavaFX graphics framework.

This is based on a white paper published by [Paul Brourke](http://paulbourke.net/fractals/dla/).

### This is the result of an example run:

![alt text](https://i.imgur.com/siDTEys.png "Example Run")

### This is a live run with large particles.

<img src="/art/dla2.gif?raw=true">

Todo:

	- Menus
		- Stop (not implemented)
		- New (not implemented)
			- Fires settings window.
	- Change color of the tree particles based on ratio of how close it is to the edge.
	- Change size of the tree particles based on ratio of how close it is to the edge.
	- Implement a stickiness factor - according to the paper, this will effect the types of fractal structures that result.
	- Implement a skip frames slider - this might help speed up generation since the program won't have to draw every frame.
