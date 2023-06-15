# Finance Tracker
### Personal use project to keep track of expenses and savings from our ING account.

This project loads the CVS file from the ING account and saves the columns in a local H2 database.
I am using H2 database in order to have this application portable for security reasons, meant to be run from a USB file.
Our main requirement is having everything offline. Hence I coded it myself, in order to keep our finance totally private.

**Progress so far as of 6/15/2023**

_Styling done with help of ChatGPT 👍_ saved a lot of time with the css and html

## Currently functional

- Transactions uploading
  - able to detect duplicates
  - if duplicates are meant to be there, you can add this manually
- Transactions 
  - add manually
  - delete it
  - not able to update due to security, prevents any errors
    - if you wish to edit, better to manually create and delete original
- Savings
  - Able to allocate savings to goals
  - Able to create Goals
    - [ ] TO DO: delete goals
    - [ ] TO DO: edit goals
  - Able to display able with savings data and summary on the goal's table
  - Able to display Goal's charts for savings
  - [ ] TO DO: yearly overview of savings in total (regardless of goals)
- Filters
  - search by keywords
  - search between dates
  - [ ] TO DO: filter keywords between dates
  - search by category
  - [ ] TO DO: filter categories between dates
  - search all those without categories
    - [ ] TO DO: fix pagination issue here
## Future implementations

- Profile creation and login 🔨 (WIP) 
- Custom CVS profiles to customize depending on the bank.
  - It will display the entity fields and and input field to map to the file's corresponding column
- Auto-categorize items
  - Via the category settings, you shoudl be able to add a list of Strings which will categorize each transaction upon uploading.
  - Each upload should then show you the list of transactions uploaded in case you need to modify any category manually
    - 💡 read last transaction ID, return from ID+ all elements in database
