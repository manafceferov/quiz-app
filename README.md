📝 Quiz Management System (Spring MVC)
Bu layihə, həm Adminlərin, həm də İştirakçıların (Participant) imtahan yarada bildiyi, lakin Adminlərin tam nəzarət yetkisinə sahib olduğu bir Spring MVC veb tətbiqidir. Layihə monolit arxitektura üzərində qurulub və backend tərəfdə güclü təhlükəsizlik və verilənlər bazası idarəetməsi tətbiq olunub.

🚀 Əsas Funksionallıqlar
İkili Rol Sistemi (RBAC): Admin və Participant üçün fərqli giriş panelləri və icazələr (Spring Security & JWT).

Quiz Yaradılması: Hər iki rol sistemdə yeni quizlər yarada bilər.

Admin Nəzarəti: Adminlər iştirakçıların yaratdığı bütün quizləri görə bilər, onlara nəzarət edə bilər və lazım gəldikdə silə bilər.

Database Versioning: Verilənlər bazası sxeminin idarə olunması üçün Liquibase istifadə edilib.

Konteynerləşdirmə: Layihə Docker vasitəsilə asanlıqla işə salına bilər.

🛠 Texnologiyalar
Framework: Spring Boot (Spring MVC, Spring Data JPA, Spring Security)

Dillər: Java və Kotlin (Entity və DTO təbəqələri üçün)

Verilənlər Bazası: PostgreSQL

Miqrasiya: Liquibase

DevOps: Docker & Docker Compose

Dokumentasiya: Swagger UI

🏗 Arxitektura: MVC Modeli
Bu layihədə Model-View-Controller (MVC) arxitekturası tətbiq olunub ki, bu da biznes məntiqinin (Business Logic) vizual hissədən (UI) tam ayrılmasını təmin edir.
