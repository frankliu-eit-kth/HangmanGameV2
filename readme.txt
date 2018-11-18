About:
	@Author: Liming(Frank) Liu
	@contact: limingl@kth.se
	@purpose of the project:
		This Hangman Game V2 project is the 2nd homework of ID1212 Network Programming course, 
		from which I learned how to use non blocking TCP sockets to build connection between the client to the server
		Also on server side I implemented single thread even loop design, all the tasks are executed in one thread,
		except that reading file will be run in a separate thread pool which executes non blocking threads
	@Code source & Copyright:
		I learned part of the structure and code from code example provided by the course: https://github.com/KTH-ID1212/nio
		Here is the license of the code example:
			/*
			 * The MIT License
			 *
			 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
			 *
			 * Permission is hereby granted, free of charge, to any person obtaining a copy
			 * of this software and associated documentation files (the "Software"), to deal
			 * in the Software without restriction, including without limitation the rights
			 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
			 * copies of the Software, and to permit persons to whom the Software is
			 * furnished to do so, subject to the following conditions:
			 *
			 * The above copyright notice and this permission notice shall be included in
			 * all copies or substantial portions of the Software.
			 *
			 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
			 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
			 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
			 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
			 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
			 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
			 * THE SOFTWARE.
			 */
	@structure:
		there are three packages in this project:
		1.client: 
			implements MVC structure:
			-model: in client.net package: to handle all the communication between the client and the server
			-an extra layer: a communication listener interface in .net package
						  It is a practice to pass the data from net layer to view layer since the net layer should not visit the view layer
			(I cancelled the controller layer since it could be replaced by communication listener)
			
			-view: in client.view package  handles the console interaction
			
			
		2. common:
			mainly set common global variables and define the message object
		
		3. server:
			implements MVC structure to server the client:
			-model: realize the game logic, totally independent from the upper level
			-controller: a game controller provides methods operated by the server's network layer
						when creating a new game controller a new non blocking thread reading word file will be created and run in a ForkJoinPool
			-view: in server.net package, handles the network communication and also executes the game logic to serve the client
		
		4. appending materials:
			words.txt: the dictionary from which the game logic reads word
			
	@What I learned:
		What I learned and achieved:
		I further mastered the MCV architecture widely used in network programming
		I learned to build connection using nonblocking TCP sockets and to switch between channels using a selector.
		I learned about single thread event loop design which improves the overall performance of the server, making the service more scalable.
		
				 