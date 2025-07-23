use bpa_lab_demonstration_factory;

create table if not exists
    vendor (
        vendor_name     varchar(255) not null
                        primary key,
        vendor_contact  varchar(255) null
);

create table if not exists
    vendor_bikecomponent (
        vendor_name         varchar(255) not null,
        bikecomponent_title varchar(255) not null,
        price               double       null,
        primary key (vendor_name, bikecomponent_title),
        constraint FKiih35bm2koc68i9y3ji8ubmcx
            foreign key (vendor_name) references vendor (vendor_name),
        constraint FKm6qiubla97d6mihosgnkgf7co
            foreign key (bikecomponent_title) references bike_component (title)
);

insert into vendor
    values ("Hans Hansen Comps",
            "hans@hansen.de"
    );
insert into vendor
    values ("Paulsen Bike Componente",
            "paul@paulsen.it"
    );

insert into vendor_bike_component
       values ("Hans Hansen Comps",
           "Citybike Component Kit", 111.11
    );
insert into vendor_bike_component
    values ("Paulsen Bike Componente",
        "Citybike Component Kit", 122.22
    );
insert into vendor_bike_component
    values ("Paulsen Bike Componente",
        "Lastenrad Component Kit", 145
    );
insert into vendor_bike_component
    values ("Paulsen Bike Componente",
            "Mountainbike Component Kit", 130
    );
