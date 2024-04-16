#DATABASE--------------------------------------------------------------------------------------
create database pi;
use pi;

#TABLEAU PANIER--------------------------------------------------------------------------------------

create table panier(
                         id int primary key auto_increment,
                         nbrProduits int,
                         totalPrix decimal(10,3)
);

insert into panier(nbrProduits, totalPrix) VALUES (2,12.5);
insert into panier(nbrProduits, totalPrix) VALUES (4,23.75);

#TABLEAU COMMANDE--------------------------------------------------------------------------------------

create table commande(
                       id int primary key auto_increment,
                       adresse varchar(20),
                       statut varchar(20) DEFAULT 'En attente',
                       delai varchar(20),
                       panier_id INT,
                       foreign key (panier_id) references panier(id)
);

INSERT INTO commande (adresse, delai, panier_id) VALUES ('123 Avenue Street', '3 jours', 1);

#TABLEAU USER--------------------------------------------------------------------------------------

create table user (
                      id int auto_increment primary key,
                      nom varchar(20) not null,
                      prenom varchar(20) not null,
                      email varchar(30) not null,
                      password varchar(20) not null,
                      adresse varchar(30),
                      role enum('visiteur', 'admin', 'livreur', 'membre') DEFAULT 'visiteur'
);

insert into user (nom, prenom, email, password, role) values ('Akkari', 'Ahmed', 'akkari.ahmed@example.com', 'password123', 'livreur');

#TABLEAU LIVRAISON--------------------------------------------------------------------------------------

create table livraisons (
                           id int auto_increment primary key,
                           livreur_id int,
                           commande_id int,
                           foreign key (livreur_id) references user(id),
                           foreign key (commande_id) references commande(id)
);

insert into livraisons (livreur_id, commande_id) values (1, 1);

#FURTHER INSERTS------------------------------------------------------------------------------------------

INSERT INTO commande (adresse, delai, panier_id) VALUES ('Tunis Ariana', '2 jours', 2);

insert into panier(nbrProduits, totalPrix) VALUES (6,60.5);

INSERT INTO commande (adresse, delai, panier_id) VALUES ('Bizerte Jarzouna', '7 jours', 3);

insert into livraisons (livreur_id, commande_id) values (2, 2);

insert into user (nom, prenom, email, password, role) values ('Jemel', 'Trabelsi', 'trabelsi.jemel@example.com', 'password456', 'livreur');

