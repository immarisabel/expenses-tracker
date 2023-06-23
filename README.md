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
  - Able to Auto Categorize transactions according to personal library of strings
- Savings
  - Able to allocate savings to goals
  - Able to create Goals
  - Able to delete goals
  - Able to edit goals
  - Able to display able with savings data and summary on the goal's table
  - Able to display Goal's charts for savings
  - Able to display yearly overview of savings in total (regardless of goals)
- Filters
  - search by keywords
  - search between dates
  - [ ] TO DO: filter keywords between dates
  - search by category
  - [ ] TO DO: filter categories between dates
  - search all those without categories
- Able to report issues and requests via GithubApi Form under /about page 
          
## Future implementations

- Profile creation and login 🔨 (WIP) 
- Custom CVS profiles to customize depending on the bank.
  - It will display the entity fields and input field to map to the file's corresponding column

