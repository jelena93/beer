CREATE TABLE IF NOT EXISTS beer_style (
  id INT AUTO_INCREMENT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  PRIMARY KEY (id)
);
--;;
INSERT INTO beer_style
(id, name, description) VALUES
(1, 'Belgian Pale Ale', '„Belgian Pale Ale“ piva nastala su kao konkurencija, češkim pivima. Odlikuje ih slaba gorčina i slatkasta aroma. Postoji dosta varijacija u ovom stilu, uglavnom zavisno od mesta proizvodnje. Procenat alkohola se kreće između 4-7%.'),
(2, 'Indian Pale Ale', '„IPA“ stil piva odlikuje izuzetna gorčina. Ta gorčina postoji zbog veće doze hmelja u ovom stilu piva. Pravljen je u 17. veku za potrebe britanskih trupa, koje su bile stacionirane u Indiji, a hmelj je dodat kao konzervans kako bi pivo izdržalo dug put.'),
(3, 'Witbier', '„Witbier“ stil odlikuje veoma svetla boja. Piva ovog stila su nefiltrirana i sadrže dosta žitarica. Veoma su pitka. Često se serviraju sa kriškom limuna, ali ako zaista želite da uživate u ukus ovog stila, ne bi trebalo da uzmete i limun. Sadrže od 4-7% alkohola.'),
(5, 'Stout/Porter', 'Iako su „Porter“  i „Stout“  dva različita stila piva, retko koji pivski ekspert bi mogao da kaže koje su to razlike. Odlikuje ih kompleksan pun ukus, često sa čokoladnom aromom ili sa aromom kafe. Procenat alkohola varira do piva do piva a u proseku je između 4-7%.'),
(6, 'Scocth Ale', '„Scotch Ale“ piva nastala su u 19. veku u Škotskoj. Odlikuju ih visoki procenti alkohola, koji su u proseku od 6-10%. Prolaze kroz dug proces karamelizacije što im daje tamnu boju. Imaju pun ukus koji takođe, dosta gorči.'),
(7, 'Belgian Strong Ale', 'Piva ovog tipa odlikuju visoki procenti alkohola, koju su u proseku između 7-12%. Imaju veoma kompleksan ukus, a odlikuje ih slatka aroma. Boja varira od svele do braon. Popularni podstilovi su: „Dubbel“, „Tripel“ i „Strong Abbey“.'),
(8, 'Bock', 'Ovo pivo potiče iz nemačkih manastira. Sadrži veće procente alkohola u proseku od 5,5-7,5%. Može biti blaži i hmeljastiji („Helles bock“) ili jači i puniji slada(„Doppelbock“). Podstil „Eisbock“, se dobija smrzavanjem i uklanjanjem leda, kako bi dobio na jačini.'),
(9, 'Pilsner', '„Pilsner“ je jedan od najpopularnijih stilova piva i rasprostranjen je širom sveta, a poreklo vodi iz Češke. Boja piva se kreće od svetle do bledo zlatne, a procenat alkohola je izmeću 4-5,5%. Karakterističan ukus, miris i gorčinu daje mu slavna vrsta češkog hmelja „SAAZ“. Služi se na temperaturi od 8°C.'),
(10, 'Kellerbier', '„Kellerbier“ predstavlja nefiltrirani i nepasterizovani nemački lager. Veoma star stil, koji datira još i srednjeg veka. Ovakva piva su bogata vitaminima. Gorčina i procenat alkohola variraju od piva do piva, a u proseku je jačina između 4-7%. '),
(11, 'Pale lager', 'Svetla piva, nastala u Nemačkoj kao odgovor na veliku popularnost čeških piva. Malo gorče, ali su blažeg i prijatnijeg ukusa. Procenat alkohola se kreće između 4-6%.'),
(12, 'Dark lager', '„Dark lager“ piva odlikuje bogat i kompleksan ukus. Pravi se koristeći tamne vrste slada, što im i daje tamnu boju. Procenat alkohola u ovim pivima varira između 4-6%. '),
(41, 'Kristalweizen', 'Predstavljaju filtriranu verziju nemačkih pšeničnih piva. Odlikuje ih čista boja od svetle do crvene i suptilan ukus banane. Procenat alkohola je između 4-7%.'),
(42, 'Heffeweizen', 'To su nefiltrirana nemačka pšenična piva. Ova piva moraju imati minimum 50% pšenice među žitaricama koje ulaze u pivo. Blaga su i pitka, a često imaju specifičan ukus koji podseća na ukus banane. Procenata alkohola je između 4-7%.'),
(43, 'Dunkelweizen', 'Ovaj stil predstavlja tamnu verziju nemačkih pšeničnih piva. Odlikuje ih kompleksan ukus blage gorčine. Kao i sva pšenična piva imaju suptilnu aromu ukusa banane. Procenat alkohola je u proseku između 4-7%.');
