#Overview#

Simple program for monitoring prices.  Currently I use it for watching my mutual funds, but could be generalized to watch any price of anything, really.  There is no GUI, it sends notifications via email.

#Running in the IDE#

After cloning the repo, you need to do a little configuration to be able to run the program:

1.  Copy (or edit directly, doesn't matter) app.template.properties.  Change the settings to be appropriate for you.  You definitely need to change the email_* settings, you could leave the unitname_* as is just to see how it works.
2.  Go into beans.xml and set the path to your app.properties file appropriately in the "properties" bean.  Ie, the file from step 1.
3.  If there are no build errors, you should be able to start fetching prices.

#Build/Deploy#

To use the provided Ant Build.xml script, you'll need to do a little editing. I have it configured to deploy into a particular "production" area.  I use some environment variables which you likely won't have.  Just have a look and change the paths to things that work for you.
