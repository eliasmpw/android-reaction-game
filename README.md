# Project Reaction Game

## Overview

The aim of this project is to develop a reaction game in android for two players, including an interaction with Android Wear service and Firebase. 
This repository contains all the files required to install and run the application.

## Structure
```
├── app                                             <- Files for the phone/tablet 
    └── java/.../reactiongame                       <- Classes, Activities, Fragments, Classes
      └── Chronometer                               <- Class to display the timer while playing
      └── EditProfileActivity                       <- Activity to Eit/Register User
      └── game4Activity                             <- Activity for game 4
      └── GameActivity                              <- Activity to pass info for game 1,2,3
      └── GameChoose                                <- Activity to get the choice and set the results
      └── GameChooseActivity                        <- Activity to get the choice and set the results
      └── GameFourActivity                          <- Game 4 
      └── GameHighscores                            <- Highscores of the game
      └── GameOption                                <- Set colors depending on the game
      └── GameResultActivity                        <- Save results and close activities
      └── LoadingActivity                           <- Loading Activiity, matchmaking process occurs here
      └── LoginActivity                             <- Login Activity, Firebase verification
      └── MainActivity                              <- Main Screen with the buttons for the games, scores, logout and exit
      └── ProfileFragment                           <- Profile Fragment
      └── Profile                                   <- Class of the profile
      └── ScoreGame1Fragment                        <- Fragments to display scores of the 4 games
      └── ScoreGame2Fragment                        <- Fragments to display scores of the 4 games
      └── ScoreGame3Fragment                        <- Fragments to display scores of the 4 games
      └── ScoreGame4Fragment                        <- Fragments to display scores of the 4 games
      └── Scores Activity                           <- Activity to have the fragments of the highscores
      └── SectionStatePagerAdapter                  <- Adapter for the fragments 
      └── SessionManager                            <- Activity to avoid login everytime and save the session of the logged user
      └── SingleScore                               <- Hash of the score with the Score, userID and username
      └── WearService                               <- Class to integrate with the watch
      └── WinLoseActivity                           <- Activity for the winning or loosing a game
    └── res                                         <- XML, drawable, values, menu 
      └── Drawable                                  <- Images used in the application
      └── Layout                                    <- Layouts for activities and fragments
        └── activity_edit_profile                   
        └── activity_game                  
        └── activity_game4                  
        └── activity_game_choose                  
        └── activity_game_result                  
        └── activity_loading
        └── activity_login
        └── activity_main
        └── activity_scores
        └── activity_win_lose
        └── fragment_my_profile
        └── fragment_score_game1
        └── fragment_score_game2
        └── fragment_score_game3
        └── fragment_score_game4
      └── menu                                      <- Menus                
        └── menu_edit_profile                                    
        └── menu_my_profile                  
      └── values                                    <- values                
        └── colors                                    
        └── strings  
        └── styles   
        
├── Wear                                            <- Files for the smartwatch
    └── java/.../reactiongame                       <- Classes, Activities, Fragments, Classes
      └── GameChooseActivity                        <- Activity choose game
      └── GameResult                                <- Result of the game
      └── HighscoresActivity                        <- Highscores
      └── LoadingActivity                           <- Loading and matchmaking
      └── MainActivity                              <- Main Activity
      └── WearService                               <- Class to integrate with the phone/tablet
    └── res                                         <- XML, drawable, values, menu 
      └── Drawable                                  <- Images used in the application
      └── Layout                                    <- Layouts for activities and fragments
        └── activity_game_choose
        └── activity_game_result
        └── activity_highscores
        └── activity_loading
        └── activity_main
      └── values                                    <- values                
        └── colors                                    
        └── strings  
        └── styles 
        
├── README.md                                       <- Readme file

```


## Setup

1. Download and unzip the file reactiongame.zip

2. Open AndroidStudio and open the project you unzipped in step 1

3. To install the app in a phone/tablet 
  - Make sure that the debugging option is enabled on the device :https://developer.android.com/studio/debug/dev-options
  - Connect your phone/tablet to the computer
  - On android studio you should be able to see your device connected 
  - Select app and then run to install the app on the device

4. To install the app in a smartwatch 
  - Make sure that the debugging option is enabled on the device : https://developer.android.com/training/wearables/apps/creating#set-up-watch
  - Connect your smartwatch to the computer 
  - On android studio you should be able to see your device connected 
  - Select wear and then run to install the app on the device

## Interaction
### Phone/Tablet
1. First step is to create a user, click on the register button
2. Fill out the fileds and pick and image, then click on save icon at the top right
3. The info you entered is already filled, just click login
4. There you can select 4 games to play, keep in mind that you need to have a friend to playwith, he/she should select the same game
5. Try to beat the oponent, at the end of the game you will go back to the main menu with the games options
6. You can play again or you can see the scores
7. If you want to exit the application click exit, if you dont click logout next time you wont need to enter your credentials again

### SmartWatch
1. First step is to create a user, click on the register button
2. Fill out the fileds and pick and image, then click on save icon at the top right
3. The info you entered is already filled, just click login
4. There you can select 4 games to play, keep in mind that you need to have a friend to playwith, he/she should select the same game
5. Try to beat the oponent, at the end of the game you will go back to the main menu with the games options
6. You can play again or you can see the scores
7. If you want to exit the application click exit, if you dont click logout next time you wont need to enter your credentials again

## Authors
- Elias Poroma Wiri
- Andres Ivan Montero Cassab
- Wang Shu

