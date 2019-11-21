# The_Labyrinth
A maze game based on the Greek Labyrinth. Can you escape the Labyrinth?

## Description
This game features a dark maze with a key gaurded by a minotaur in the center of the maze. The player starts at the exit, so all they must do is go to the center, steal the key, and exit the maze while avoiding the minotaur. An interesting feature in this game is that the visible area to the player is only the ray in front of the player, and the view may be blocked by walls or obstacles. The main menu screen is shown below:

![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Main%20Menu.PNG "Main Menu")

The instructions for the game are shown if the user clicks on "Help", as shown below:

![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Help%20Page%201.PNG "Help Page 1")
![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Help%20Page%202.PNG "Help Page 2")
![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Help%20Page%203.PNG "Help Page 3")
![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Help%20Page%204.PNG "Help Page 4")
![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Help%20Page%205.PNG "Help Page 5")
![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Help%20Page%206.PNG "Help Page 6")

An example of gameplay is shown below:

![](https://github.com/dipeshmanandhar/The_Labyrinth/raw/master/pics/Gameplay/Gameplay.png "Gameplay")

## Maze Generation Algorithm
The maze was created using a randomized version of Prim's Algorithm, the same one as used in my Empty Life Project (https://github.com/dipeshmanandhar/Empty_Life).

## 2D Visibility Algorithm
The visible area of the player was created by superimposing a ray (cone) on top of the shape created by a Raycasting algorithm that sends vectors to all nearby wall's corners. This algorithm was optimized through many ways, including only looking at nearby maze cells and parallizing most of the work onto multiple threads.


This project was completed by May 2018
