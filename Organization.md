# Team Members Contribution
### Matt (UPI: mjam534)
Matt was the sole person responsible for the domain model, he also helped out others if they came across any issues while working on the ConcertResource.
### Para (UPI: pbal978)
Para was the key person responsible for the methods to do with bookings, login and subscribing in the ConcertResource, as well as being partly responsible for the concert and performer methods.
### Lok (UPI: tkwo024)
Lok was the key person responsible for the methods to do with the seats and publishing, as well as being partly responsible for the concert and performer methods.  

# Strategy to Minimise Concurrency Errors
For concurrent database access if the use case only requires reading from the database no control is used. This is because even if the data is slightly out of date it isn’t operated on and committed back in nor are any internal decisions based on it. However if the accessed item is then operated on and committed or a derivative is committed the item will be optimistically locked to allow multiple things to operate on it but only allow the first commit to actually occur and any commits after the first one will be met with an optmistic lock exception.
# Domain Model Oranisation
The general strategy of the domain model is to default to lazy fetching whenever possible to not hurt performance. Where an eager fetch strategy is more effective a select fetch mode is used to minimise the impact on performance. Furthermore, all relationships between classes utilise a cascade strategy, most of these use a combination of persist and remove. So, changes are propagated but also unused items are removed.