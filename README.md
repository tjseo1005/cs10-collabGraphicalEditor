# README file for cs10-collabGraphicalEditor

(Sections of the following description is adapted from the official course instructions found here: https://www.cs.dartmouth.edu/~albertoq/cs10/PS-6.html) 

This file contains all the code produced for Problem Set 6 of my CS10 coursework at Dartmouth College. 

I implement a collaborative graphical editor — a much more rudimentary version of Google Slides, but nonetheless one that can enable multiple clients to connect a server and collaboratively make graphical edits that can be seen by other clients. This code handles multiple objects at a time, and can draw rectangles, line segments, and "freehand" shapes in addition to ellipses.

The basic client/server set-up is as follows. Each client editor has a thread for talking to the sketch server, along with a main thread for user interaction. The server has a main thread to get the incoming requests to join the shared sketch, along with separate threads for communicating with the clients. The client tells the server about its user's drawing actions. The server then tells all the clients about all the drawing actions of each of them.

My code is implemented on top of a minimal scaffold code provided by the instructor. You can see the contents of the scaffold code in editor.zip in the above course website. You can assume all other code to have been programmed by myself with some theoretical help provided by my lab partner, Stephen Adjel (we agreed that I should implement most of Pset 6). 

Some explanation on the accompanying files: 

Editor: client, handling GUI-based drawing interaction
EditorOne: a sample program to get you started on the Editor
EditorCommunicator: for messages to/from the server
SketchServer: central keeper of the master sketch; synchronizing the various Editors
SketchServerCommunicator: for messages to/from a single editor (one for each such client)
EchoServer: an alternative server useful for development / debugging
Shape: interface for a graphical shape (with color), with implementations Ellipse, Polyline, Rectangle, and Segment (each implemented in their own Java file)
