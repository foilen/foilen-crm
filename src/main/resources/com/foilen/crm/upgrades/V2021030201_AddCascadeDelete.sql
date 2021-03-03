ALTER TABLE item DROP FOREIGN KEY `FKpxnjeqy0c2uq7xqdt6t76flj6`;
alter table item 
   add constraint FKpxnjeqy0c2uq7xqdt6t76flj6 
   foreign key (client_id) 
   references client (id) 
   on delete cascade;

ALTER TABLE recurrent_item DROP FOREIGN KEY `FK4gtjj1276txn1630xwc778g22`;
alter table recurrent_item 
   add constraint FK4gtjj1276txn1630xwc778g22 
   foreign key (client_id) 
   references client (id) 
   on delete cascade;

ALTER TABLE transaction DROP FOREIGN KEY `FK7j4eee09p60fngm038fc4oxj2`;
alter table transaction 
   add constraint FK7j4eee09p60fngm038fc4oxj2 
   foreign key (client_id) 
   references client (id) 
   on delete cascade;
