
SAVE THE TEACHER - Android Application

https://user-images.githubusercontent.com/25661319/27577265-b067d7b2-5b28-11e7-9de5-a9da14f10b48.jpg

1.Εισαγωγή

Η παρούσα εργασία πραγματοποιήθηκε στα πλαίσια της απαλλακτικής εργασίας του Β&#39; Εξαμήνου στο μάθημα Κινητή Υπολογιστική και Εφαρμογές με θέμα: «Ανάπτυξη Εφαρμογής σε Android».

2.Γενική Ιδέα Εφαρμογής

Η εφαρμογή αποτελεί μία κοινωνική πλατφόρμα στοχευμένη για τους αναπληρωτές δασκάλους ανά την Ελλάδα. Η γενική ιδέα είναι ότι ο κάθε δάσκαλος (που κάθε χρόνο βρίσκεται σε διαφορετικό σχολείο - αναπληρωτής) κάνοντας ένα login στην εφαρμογή μπορεί να δημοσιεύει το μέρος στο οποίο τον πήρανε για εργασία ή να κοινοποιεί διάφορους προβληματισμούς του, ώστε να βρει κι άλλους δασκάλους προκειμένου να &quot;κοινωνικοποιηθεί&quot; στην νέα του περιοχή (για παράδειγμα να κανονίσει να πηγαίνουν με κοινό αυτοκίνητο στο σχολείο). Επιπροσθέτως, μπορεί να δημοσιεύει διάφορες σκέψεις προς όσους τον ακολουθούν και να βλέπει «σκέψεις» αυτών που ακολουθεί ή να κάνει αναζητήσεις με λέξεις κλειδιά που τον ενδιαφέρουν. Επίσης παρέχεται η δυνατότητα πρόγνωσης του καιρού μέσα από την εφαρμογή. Τέλος υπάρχει η δυνατότητα ο δάσκαλος να αναζητήσει όλες τις περιοχές, τις διευθύνσεις και τα σχολεία σε κάθε πρωτοβάθμια διεύθυνση της Ελλάδος.

3.Τεχνολογίες Υλοποίησης

Στην εφαρμογή χρησιμοποιήθηκε ένας μεγάλος αριθμός τεχνολογιών, οι οποίες διδάχθηκαν εντός των εργαστηρίων κατά την διάρκεια των μαθημάτων του Β΄ εξαμήνου στο συγκεκριμένο μάθημα. Η εφαρμογή υλοποιήθηκε με AndroidStudio.

Οι τεχνολογίες επιγραμματικά είναι οι κάτωθι:

- VirtualMachine (VM) : Δημιουργήθηκε για τις ανάγκες της εφαρμογής ένα VM στον ~keanos ( [https](https://okeanos.grnet.gr/home/) [://](https://okeanos.grnet.gr/home/) [keanos](https://okeanos.grnet.gr/home/) [.](https://okeanos.grnet.gr/home/) [grnet](https://okeanos.grnet.gr/home/) [.](https://okeanos.grnet.gr/home/) [gr](https://okeanos.grnet.gr/home/) [/](https://okeanos.grnet.gr/home/) [home](https://okeanos.grnet.gr/home/) [/](https://okeanos.grnet.gr/home/)), όπου στήθηκε ένας MicrosoftWindowsServer 2012, εγκαταστάθηκε το WAMP και ρυθμίστηκε η κατάλληλη πόρτα (localhost:8083) καθώς και τα κατάλληλα configurationfiles του apache, όπως και το firewall του server.
- MYSQL: Φτιάχτηκε βάση δεδομένων(ΒΔ) σε SQL στον παραπάνω server
- PHP: Υλοποιήθηκαν εφτά (7) scripts σε php αρχεία για την διαχείριση της ΒΔ. (βρίσκονται στο φάκελο php_Files του project: https://github.com/Spiroskleft/SaveTheTeacher/tree/master/php_Files)
- FirebaseLogin: Χρησιμοποιήθηκε το firebase για Login στην εφαρμογή
- GoogleLogin: Χρησιμοποιήθηκε το GoogleSignIn για εναλλακτικό Login των χρηστών.
- FirebaseNotifications: Χρησιμοποιήθηκε η τεχνολογία FirebaseCloudMessaging για να στέλνονται notifications στην εφαρμογή
- Realm: Χρησιμοποιήθηκε το Realm για διαχείριση ενός JSON αρχείου με τις περιοχές των αναπληρωτών και δημιουργία από αυτό ενός RealmObject και στη συνέχεια κατάλληλης λίστας με τις περιοχές.
- Picasso: Χρησιμοποίηση του Picasso για εισαγωγή φωτογραφιών στη δημοσίευση σκέψεων.
- FirebaseStorage: Αποθήκευση φωτογραφιών των χρηστών στο firebasestorage συγκεκριμένα στο &quot;gs://savetheteacherapp-55d47.appspot.com&quot;
- RetrofitAPI: Έχει υλοποιηθεί με retrofitapi του [http://openweathermap.org/](http://openweathermap.org/) για πρόγνωση καιρού μέσα από την εφαρμογή τόσο για τον καιρό που επικρατεί όσο και για πρόγνωση εφτά ημερών. Επίσης έχει υλοποιηθεί με άλλο api η αποτύπωση καιρού και συντεταγμένων οποιασδήποτε περιοχής ερωτήσει ο χρήστης.
- FirebaseCrashReporting: Χρησιμοποίηση για έλεγχο των σφαλμάτων της εφαρμογής που έχει εγκατασταθεί στους users.



Αναλυτικότερα ανατρέξτε στον κώδικα της εφαρμογής: https://github.com/Spiroskleft/SaveTheTeacher/blob/master/app/src/main/java/com/example/spiros/savetheteacher/

Ολόκληρο το documentation της εφαρμογής μπορεί να βρεθεί στον φάκελο Documentation του project: https://github.com/Spiroskleft/SaveTheTeacher/tree/master/Documentation/

To APK της εφαρμογής βρίσκεται στον σύνδεσμο https://github.com/Spiroskleft/SaveTheTeacher/tree/master/APK/
