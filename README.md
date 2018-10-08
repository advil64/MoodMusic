# MoodMusic
MoodMusic - Music based on weather

##Inspiration
We like to listen to music and we were interested in how the weather can affect the mood of an individual. This is seen in seasonal affective disorder (SAD) where an individual can have shifting moods with the changes in season. We are also aware of the fact that many people enjoy listening to sad music when they are sad as it allows them to cope with their feelings. Thus, we thought why not have an app that allows you to listen to music in one button based off the mood you may feel with the weather outside.

##What it does
Using the OpenWeatherMap API and the SoundCloud API, Mood Music allows one to check the weather and temperature, and listen to music that corresponds to that weather. For example, when it is sunny outside, people typically feel elated and would want to listen to upbeat music. With Mood Music, the user is only one button away from listening to upbeat music on a sunny day or slow music on a rainy day.

##How we built it
This app was built in android studio. Using android native functions, the app tracks the user's location (with permission) by first using the user's wifi to estimate the location of the user. If that doesn't work, the app uses cell phone towers to estimate the user's location. If that still doesn't work, the app will refer back to the user's previous location. The app then enters the user's coordinates into the OpenWeatherMap API database to find the current weather and temperature of the location. This information is then retrieved by reading the online JSON file. The corresponding icons are also read and retrieved from the OpenWeatherMap API. We then implemented a SoundCloud player into the app to play music. Finally, we corresponded some music to match the mood of the weather.

##Challenges we ran into
In regards to the OpenWeatherAPI, one challenge we ran into was reading the information from the database. This proved difficult as android studio doesn't allow URLs to be read in the Main UI. Thus, in order to bypass this rule, the code to read URLs were done in the background by using an AsyncTask class to run it. In terms of finding the user's location, the challenge in this was finding the quickest way to get the user's coordinates. We had to experiment with multiple methods of finding the user's location. We eventually settled for a method that is new and the fastest out of all the methods used.

##Accomplishments that we're proud of
One accomplishment we're proud of is the fact that we were able to make an app that can find the user and be able to read from online sources for information. We're also proud of how the app looks and how it works properly. Most importantly, we're proud of the fact that we were able to program this app and still manage to get at least six hours of sleep.

##What we learned
One thing we learned is how to use wifi and cell phone signals to find the user and their coordinates. Another thing we learned is the fact that android studio doesn't allow reading URLs in the Main Activity. However, we learned to bypass this rule by reading URLs in the background using AsyncTask.

##What's next for Mood Music
In the near future, we would like to expand our music library for each of the weather conditions. Later on in the future, we would like to utilize more features of the weather (humidity, sun's location) and the user (heartbeat, etc.) to better determine the user's mood and play more accurate music based off that. In addition, we would like to make the moods and the weather more customizable as some individuals are happy in the rain and depressed when it is sunny. Further on in the future, we would like to utilize machine learning to see the most popular music for each weather and other features, in order to suggest the more popular songs.

