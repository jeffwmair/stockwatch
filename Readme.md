#Overview#

Simple program for monitoring prices.  Currently I use it for watching my mutual funds, but could be generalized to watch any price of anything, really.  There is no GUI, it sends notifications via email.

#Running

After cloning the repo, you need to do a little configuration to be able to run the program:

1.  Copy (or edit directly, doesn't matter) app.template.properties.  Change the settings to be appropriate for you.  You definitely need to change the email_* settings, you could leave the unitname_* as is just to see how it works.
2.  Go into beans.xml and set the path to your app.properties file appropriately in the "properties" bean.  Ie, the file from step 1.
3.  If there are no build errors, you should be able to start fetching prices.

#Build/Deploy#

The maven copy-dependencies plugin will copy the necessary jars into a lib/ directory during the package phase.  I have a deploy.sh script that copies everything to the "prod" directory. You would need to modify the paths in that script.
