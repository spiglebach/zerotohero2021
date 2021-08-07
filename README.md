# Zero To Hero 2021
## Szukács Marcell
### Endpoints
- /inventory
  - POST - creates a new Inventory  
  Request body example: {  
  "employeeId":"3GC0BJRWFK7RP209",  "itemId":"3GC0BJRY8XRCS30O",  "epochSecond":1628365431  
  }
  - GET - gets the list all Inventories
- /inventory/{id}
  - GET - gets the Inventory that has the specified id
  - PUT - updates the Inventory that has the specified id  
  Request body example: {  
  "employeeId":"3GC0BJRWFK7RP209",  "itemId":"3GC0BJRY8XRCS30O",  "epochSecond":1628365431  
  }
- /inventory/check
  - POST - starts an "Inventory Check" that compares all Inventory objects against the 
  "original inventory requests" and updates them accordingly