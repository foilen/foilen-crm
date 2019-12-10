
    create table client (
       id bigint not null auto_increment,
        address varchar(255),
        contact_name varchar(255),
        email varchar(255),
        lang varchar(255),
        main_site varchar(255),
        name varchar(255) not null,
        short_name varchar(255) not null,
        tel varchar(255),
        version bigint not null,
        technical_support_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table item (
       id bigint not null auto_increment,
        category varchar(255),
        date datetime,
        description varchar(2000),
        invoice_id varchar(255),
        price bigint not null,
        version bigint not null,
        client_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table recurrent_item (
       id bigint not null auto_increment,
        calendar_unit integer not null,
        category varchar(255),
        delta integer not null,
        description varchar(2000),
        next_generation_date datetime,
        price bigint not null,
        version bigint not null,
        client_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table technical_support (
       id bigint not null auto_increment,
        price_per_hour bigint not null,
        sid varchar(10) not null,
        version bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table transaction (
       id bigint not null auto_increment,
        date datetime,
        description varchar(255),
        invoice_id varchar(255),
        price bigint not null,
        version bigint not null,
        client_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table user (
       id bigint not null auto_increment,
        is_admin bit not null,
        email varchar(255),
        user_id varchar(255) not null,
        version bigint not null,
        primary key (id)
    ) engine=InnoDB;

    alter table client 
       add constraint UK_dn5jasds5r1j3ewo5k3nhwkkq unique (name);

    alter table client 
       add constraint UK_epk5nw87ng8rnd46cs3eb2dm3 unique (short_name);

    alter table technical_support 
       add constraint UK_1xc923jrqhm36dayif6e9sjit unique (sid);

    alter table transaction 
       add constraint UK_70ee1w3sw3hsac0yv8bidsyg0 unique (invoice_id);

    alter table user 
       add constraint UK_a3imlf41l37utmxiquukk8ajc unique (user_id);

    alter table client 
       add constraint FK2mi1tp6expksgkk6bq3o79s43 
       foreign key (technical_support_id) 
       references technical_support (id);

    alter table item 
       add constraint FKpxnjeqy0c2uq7xqdt6t76flj6 
       foreign key (client_id) 
       references client (id);

    alter table recurrent_item 
       add constraint FK4gtjj1276txn1630xwc778g22 
       foreign key (client_id) 
       references client (id);

    alter table transaction 
       add constraint FK7j4eee09p60fngm038fc4oxj2 
       foreign key (client_id) 
       references client (id);
