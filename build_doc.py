# -*- coding: utf-8 -*-
"""Generate a detailed Word guide for completing the Car Renting System project."""
from docx import Document
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

OUT = r"D:\HSF302_Asm\carrentingsystem\HuongDan_HoanThien_Project.docx"

doc = Document()

# ---- Base style ----
normal = doc.styles["Normal"]
normal.font.name = "Calibri"
normal.font.size = Pt(11)
normal._element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")

# Code style
if "CodeBlock" not in [s.name for s in doc.styles]:
    cs = doc.styles.add_style("CodeBlock", 1)  # paragraph style
    cs.font.name = "Consolas"
    cs.font.size = Pt(9)
    cs.base_style = doc.styles["Normal"]

ACCENT = RGBColor(0x1F, 0x4E, 0x79)
CODE_BG = "F2F2F2"


def shade(paragraph, fill):
    pPr = paragraph._p.get_or_add_pPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:fill"), fill)
    pPr.append(shd)


def code(text):
    """Add a shaded monospace code block (multi-line)."""
    p = doc.add_paragraph(style="CodeBlock")
    shade(p, CODE_BG)
    pf = p.paragraph_format
    pf.left_indent = Inches(0.15)
    pf.space_before = Pt(4)
    pf.space_after = Pt(8)
    pf.line_spacing = 1.0
    run = p.add_run(text)
    run.font.name = "Consolas"
    run.font.size = Pt(9)
    return p


def para(text, bold=False, italic=False, size=11, color=None, space_after=6):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(space_after)
    r = p.add_run(text)
    r.bold = bold
    r.italic = italic
    r.font.size = Pt(size)
    if color:
        r.font.color.rgb = color
    return p


def bullet(text, bold_prefix=None):
    p = doc.add_paragraph(style="List Bullet")
    p.paragraph_format.space_after = Pt(3)
    if bold_prefix:
        r = p.add_run(bold_prefix)
        r.bold = True
        p.add_run(text)
    else:
        p.add_run(text)
    return p


def numbered(text):
    p = doc.add_paragraph(style="List Number")
    p.paragraph_format.space_after = Pt(3)
    p.add_run(text)
    return p


def h(text, level=1):
    p = doc.add_heading(text, level=level)
    for r in p.runs:
        r.font.color.rgb = ACCENT
    return p


def table(headers, rows, widths=None):
    t = doc.add_table(rows=1, cols=len(headers))
    t.style = "Light Grid Accent 1"
    t.alignment = WD_TABLE_ALIGNMENT.CENTER
    hdr = t.rows[0].cells
    for i, htext in enumerate(headers):
        hdr[i].text = ""
        run = hdr[i].paragraphs[0].add_run(htext)
        run.bold = True
        run.font.size = Pt(10)
    for row in rows:
        cells = t.add_row().cells
        for i, val in enumerate(row):
            cells[i].text = ""
            r = cells[i].paragraphs[0].add_run(str(val))
            r.font.size = Pt(9.5)
    if widths:
        for i, w in enumerate(widths):
            for row in t.rows:
                row.cells[i].width = Inches(w)
    doc.add_paragraph().paragraph_format.space_after = Pt(2)
    return t


# ===================== TITLE PAGE =====================
title = doc.add_paragraph()
title.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = title.add_run("HUONG DAN HOAN THIEN PROJECT")
r.bold = True
r.font.size = Pt(24)
r.font.color.rgb = ACCENT
title.runs[0].text = "HƯỚNG DẪN HOÀN THIỆN PROJECT"

sub = doc.add_paragraph()
sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = sub.add_run("FU Car Renting Management System")
r.bold = True
r.font.size = Pt(16)

sub2 = doc.add_paragraph()
sub2.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = sub2.add_run("Spring Boot · Spring Data JPA · Thymeleaf · SQL Server")
r.italic = True
r.font.size = Pt(12)

doc.add_paragraph()
meta_tbl = doc.add_table(rows=0, cols=2)
meta_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
for k, v in [
    ("Môn học", "HSF302 - Assignment 01"),
    ("Project Name", "SE2035-JV_<StudentId>_CarRentingSystem"),
    ("Database", "CarRentingSystem_DB (SQL Server)"),
    ("User / Pass DB", "test / test"),
    ("Deadline", "19/Jul"),
    ("Ngày tạo tài liệu", "29/06/2026"),
]:
    row = meta_tbl.add_row().cells
    rr = row[0].paragraphs[0].add_run(k)
    rr.bold = True
    rr.font.size = Pt(10)
    row[1].paragraphs[0].add_run(v).font.size = Pt(10)

doc.add_page_break()

# ===================== MUC LUC =====================
h("Mục lục", 1)
for it in [
    "1. Tổng quan & mục tiêu Assignment",
    "2. Hiện trạng project (đã có những gì)",
    "3. Khoảng cách cần lấp (Gap analysis)",
    "4. Kiến trúc & cấu trúc thư mục đích",
    "5. Bước 1 - Cấu hình & Database",
    "6. Bước 2 - Cập nhật Entity cho khớp schema v2.0",
    "7. Bước 3 - Repository layer",
    "8. Bước 4 - Service layer",
    "9. Bước 5 - Controller & phân quyền",
    "10. Bước 6 - Giao diện Thymeleaf",
    "11. Bước 7 - Các chức năng nghiệp vụ chính",
    "12. Bước 8 - Báo cáo thống kê",
    "13. Bước 9 - Security & đăng nhập",
    "14. Bước 10 - Kiểm thử & chạy thử",
    "15. Checklist nghiệm thu theo rubric",
    "16. Phụ lục - lệnh & mẹo",
]:
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(2)
    p.add_run(it).font.size = Pt(11)
doc.add_page_break()

# ===================== 1. TONG QUAN =====================
h("1. Tổng quan & mục tiêu Assignment", 1)
para("Assignment yêu cầu xây dựng một website quản lý cho thuê xe (FU Car Renting "
     "Management System) bằng Spring Boot + Spring Data JPA + Thymeleaf, lưu dữ liệu "
     "trong SQL Server. Ứng dụng phải làm đầy đủ CRUD cho các đối tượng và phân quyền "
     "theo 2 vai trò Admin / Customer.")

h("1.1. Vai trò Admin được phép", 2)
for t in [
    "Quản lý thông tin khách hàng (Customer).",
    "Quản lý thông tin xe (Car). Khi xoá: nếu xe CHƯA thuộc giao dịch thuê nào thì xoá "
    "hẳn; nếu ĐÃ nằm trong giao dịch thì chỉ đổi Status (soft-delete).",
    "Quản lý các giao dịch thuê xe (CarRental).",
    "Tạo báo cáo thống kê giao dịch theo khoảng thời gian StartDate đến EndDate, sắp "
    "xếp giảm dần.",
]:
    bullet(t)

h("1.2. Vai trò Customer được phép", 2)
for t in [
    "Đăng ký tài khoản (Register).",
    "Tạo giao dịch thuê online với một hoặc nhiều xe.",
    "Quản lý hồ sơ cá nhân (profile).",
    "Xem lịch sử giao dịch của mình.",
]:
    bullet(t)

para("Yêu cầu kỹ thuật bắt buộc (theo file Lưu ý.txt):", bold=True)
table(
    ["Hạng mục", "Quy định"],
    [
        ["Project name", "SE2035-JV_<StudentId>_CarRentingSystem"],
        ["Database name", "CarRentingSystem_DB"],
        ["DB user / pass", "test / test"],
        ["ddl-auto", "none (tạo DB bằng script, không để Hibernate tự sinh)"],
        ["Kiểu ID", "Long (BIGINT IDENTITY)"],
        ["Trường name", "NVARCHAR(255); riêng AccountName: VARCHAR(255)"],
        ["Trường ngày", "LocalDate; riêng PickupDate/ReturnDate: LocalDateTime"],
    ],
    widths=[2.0, 4.0],
)
print("section 1 done")

# ===================== 2. HIEN TRANG =====================
h("2. Hiện trạng project (đã có những gì)", 1)
para("Project hiện đã dựng được khung Spring Boot và chạy được luồng đọc danh sách xe. "
     "Tóm tắt những phần đã hoàn thành:")
table(
    ["Lớp / thành phần", "File", "Tình trạng"],
    [
        ["Entity", "Account, Car, CarProducer, CarRental(+Id), Customer, Review(+Id)", "Có, nhưng theo schema CŨ"],
        ["Repository", "CarRepository, AccountRepository, CustomerRepository, ...", "Có (rỗng, mới extends JpaRepository)"],
        ["Service", "CarService + CarServiceImpl", "Chỉ có cho Car"],
        ["Controller", "CarController (GET /cars)", "Chỉ liệt kê xe"],
        ["View", "templates/car/car-list.html", "Chỉ 1 trang danh sách"],
        ["Cấu hình", "application.properties", "Trỏ DB FUCarRentingSystemDB, user sa"],
        ["Script DB", "db/CarRentingSystem_DB.sql", "ĐÃ tạo theo v2.0 + dữ liệu mẫu"],
    ],
    widths=[1.7, 2.8, 1.6],
)
para("Cổng chạy hiện tại: server.port = 9999. URL danh sách xe: "
     "http://localhost:9999/cars", italic=True)

# ===================== 3. GAP =====================
h("3. Khoảng cách cần lấp (Gap analysis)", 1)
para("Đây là phần quan trọng nhất. Code entity hiện tại KHÔNG khớp với schema v2.0 "
     "trong đề và với file Lưu ý.txt. Nếu chạy app với DB mới sẽ lỗi mapping Hibernate. "
     "Bảng dưới liệt kê mọi điểm lệch cần sửa:")
table(
    ["Vấn đề", "Hiện tại (code)", "Cần sửa thành (v2.0 + Lưu ý)"],
    [
        ["Kiểu khoá chính", "Integer", "Long (BIGINT)"],
        ["Account", "Chỉ AccountName, Role", "Thêm Email, Password"],
        ["Customer", "Có customerName, email, password", "Đổi sang fullName; bỏ email/password"],
        ["Quan hệ Customer-Account", "@OneToOne 2 chiều", "Giữ 1 chiều, AccountID unique"],
        ["CarRental khoá", "@EmbeddedId (Customer+Car+Pickup)", "Surrogate carRenID (BIGINT IDENTITY)"],
        ["CarRental ngày", "LocalDate", "LocalDateTime (PickupDate/ReturnDate)"],
        ["Review khoá", "@EmbeddedId (Customer+Car)", "Surrogate id; tham chiếu CarRental"],
        ["Review quan hệ", "ManyToOne Customer + Car", "ManyToOne CarRental (CarRentID)"],
        ["Bỏ file", "CarRentalId.java, ReviewId.java", "Xoá (không còn dùng)"],
        ["application.properties", "DB FUCarRentingSystemDB, sa/sa", "CarRentingSystem_DB, test/test"],
    ],
    widths=[1.6, 2.2, 2.4],
)
para("Ngoài ra còn THIẾU toàn bộ các phần sau (chưa code dòng nào):", bold=True)
for t in [
    "Service + Controller cho Customer, CarProducer, CarRental, Review, Account.",
    "Chức năng đăng ký / đăng nhập + phân quyền Admin / Customer (Spring Security).",
    "Form thêm/sửa/xoá (CRUD) trên giao diện cho từng đối tượng.",
    "Luồng đặt thuê online nhiều xe của Customer.",
    "Trang profile + lịch sử giao dịch của Customer.",
    "Báo cáo thống kê giao dịch theo khoảng ngày, sắp xếp giảm dần.",
    "Layout/giao diện chung (header, menu, CSS).",
]:
    bullet(t)
print("section 2-3 done")

# ===================== 4. KIEN TRUC =====================
h("4. Kiến trúc & cấu trúc thư mục đích", 1)
para("Áp dụng mô hình 3 lớp + Repository Pattern: Controller → Service → Repository → "
     "Database. Entity là model dùng chung. View dùng Thymeleaf.")
code(
"src/main/java/com/assignment/carrentingsystem/\n"
" ├─ CarrentingsystemApplication.java\n"
" ├─ config/        SecurityConfig.java (phân quyền)\n"
" ├─ controller/    Car, Customer, CarProducer, CarRental, Review,\n"
" │                 Auth(Login/Register), Report, Home controllers\n"
" ├─ service/       interface cho từng nghiệp vụ\n"
" │   └─ impl/      cài đặt service\n"
" ├─ repository/    JpaRepository cho từng entity\n"
" ├─ entity/        Account, Car, CarProducer, Customer, CarRental, Review\n"
" └─ dto/           RentalReportDTO, RegisterForm, ... (tuỳ chọn)\n"
"\n"
"src/main/resources/\n"
" ├─ application.properties\n"
" ├─ db/CarRentingSystem_DB.sql\n"
" ├─ static/        css, js\n"
" └─ templates/     fragments/layout.html + thư mục theo module\n"
"     ├─ car/ customer/ producer/ rental/ review/ report/\n"
"     └─ auth/ (login.html, register.html)"
)
para("Nguyên tắc: Controller KHÔNG gọi thẳng Repository. Mọi nghiệp vụ (kiểm tra xe đã "
     "có giao dịch chưa, tính tổng tiền thuê...) đặt ở Service.", italic=True)

# ===================== 5. CONFIG & DB =====================
h("5. Bước 1 - Cấu hình & Database", 1)
h("5.1. Chạy script tạo DB", 2)
para("Mở SSMS / Azure Data Studio bằng tài khoản sa, mở file db/CarRentingSystem_DB.sql "
     "và Execute. Script tự tạo DB, login test/test, các bảng và dữ liệu mẫu. Chạy lại "
     "nhiều lần được (idempotent).")
para("Hoặc dùng sqlcmd:")
code('sqlcmd -S localhost -U sa -P <pass_sa> -i '
     '"src\\main\\resources\\db\\CarRentingSystem_DB.sql"')

h("5.2. Sửa application.properties", 2)
para("Đổi connection string sang DB và tài khoản đúng yêu cầu:")
code(
"spring.application.name=carrentingsystem\n"
"server.port=9999\n"
"\n"
"spring.datasource.url=jdbc:sqlserver://localhost:1433;"
"databaseName=CarRentingSystem_DB;encrypt=true;trustServerCertificate=true\n"
"spring.datasource.username=test\n"
"spring.datasource.password=test\n"
"spring.datasource.driver-class-name="
"com.microsoft.sqlserver.jdbc.SQLServerDriver\n"
"\n"
"spring.jpa.hibernate.ddl-auto=none\n"
"spring.jpa.show-sql=true\n"
"spring.jpa.properties.hibernate.format_sql=true\n"
"spring.thymeleaf.cache=false"
)
para("Lưu ý: giữ ddl-auto=none đúng yêu cầu. Đảm bảo pom.xml có dependency "
     "mssql-jdbc, spring-boot-starter-data-jpa, spring-boot-starter-thymeleaf, "
     "spring-boot-starter-web, spring-boot-starter-security, lombok.")
print("section 4-5 done")

# ===================== 6. ENTITY =====================
h("6. Bước 2 - Cập nhật Entity cho khớp schema v2.0", 1)
para("Sửa các entity hiện có cho khớp DB mới. Dưới đây là code đầy đủ cho từng file.")

h("6.1. Account.java", 2)
code(
"@Entity @Table(name=\"Account\")\n"
"@Getter @Setter @NoArgsConstructor @AllArgsConstructor\n"
"public class Account {\n"
"  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)\n"
"  @Column(name=\"AccountID\") private Long accountId;\n"
"  @Column(name=\"AccountName\", nullable=false, length=255)\n"
"  private String accountName;\n"
"  @Column(name=\"Email\", nullable=false, length=255, unique=true)\n"
"  private String email;\n"
"  @Column(name=\"Password\", nullable=false, length=255)\n"
"  private String password;\n"
"  @Column(name=\"Role\", nullable=false, length=20)\n"
"  private String role;   // 'Admin' / 'Customer'\n"
"}"
)

h("6.2. CarProducer.java", 2)
code(
"@Entity @Table(name=\"CarProducer\")\n"
"@Getter @Setter @NoArgsConstructor @AllArgsConstructor\n"
"public class CarProducer {\n"
"  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)\n"
"  @Column(name=\"ProducerID\") private Long producerId;\n"
"  @Column(name=\"ProducerName\", nullable=false, length=255)\n"
"  private String producerName;\n"
"  @Column(name=\"Address\", nullable=false, length=255) private String address;\n"
"  @Column(name=\"Country\", nullable=false, length=255) private String country;\n"
"  @OneToMany(mappedBy=\"carProducer\") private List<Car> cars;\n"
"}"
)

h("6.3. Car.java", 2)
code(
"@Entity @Table(name=\"Car\")\n"
"@Getter @Setter @NoArgsConstructor @AllArgsConstructor\n"
"public class Car {\n"
"  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)\n"
"  @Column(name=\"CarID\") private Long carId;\n"
"  @Column(name=\"CarName\", nullable=false, length=255) private String carName;\n"
"  @Column(name=\"CarModelYear\", nullable=false) private Integer carModelYear;\n"
"  @Column(name=\"Color\", nullable=false, length=50) private String color;\n"
"  @Column(name=\"Capacity\", nullable=false) private Integer capacity;\n"
"  @Column(name=\"Description\", nullable=false, length=500) private String description;\n"
"  @Column(name=\"ImportDate\", nullable=false) private LocalDate importDate;\n"
"  @Column(name=\"RentPrice\", nullable=false, precision=18, scale=2)\n"
"  private BigDecimal rentPrice;\n"
"  @Column(name=\"Status\", nullable=false, length=20) private String status;\n"
"  @ManyToOne(fetch=FetchType.LAZY)\n"
"  @JoinColumn(name=\"ProducerID\", nullable=false) private CarProducer carProducer;\n"
"}"
)

h("6.4. Customer.java", 2)
code(
"@Entity @Table(name=\"Customer\")\n"
"@Getter @Setter @NoArgsConstructor @AllArgsConstructor\n"
"public class Customer {\n"
"  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)\n"
"  @Column(name=\"CustomerID\") private Long customerId;\n"
"  @Column(name=\"FullName\", nullable=false, length=255) private String fullName;\n"
"  @Column(name=\"Mobile\", nullable=false, length=20) private String mobile;\n"
"  @Column(name=\"Birthday\", nullable=false) private LocalDate birthday;\n"
"  @Column(name=\"IdentityCard\", nullable=false, length=20) private String identityCard;\n"
"  @Column(name=\"LicenceNumber\", nullable=false, length=20) private String licenceNumber;\n"
"  @Column(name=\"LicenceDate\", nullable=false) private LocalDate licenceDate;\n"
"  @OneToOne(fetch=FetchType.LAZY)\n"
"  @JoinColumn(name=\"AccountID\", nullable=false, unique=true) private Account account;\n"
"}"
)

h("6.5. CarRental.java (khoá surrogate)", 2)
code(
"@Entity @Table(name=\"CarRental\")\n"
"@Getter @Setter @NoArgsConstructor @AllArgsConstructor\n"
"public class CarRental {\n"
"  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)\n"
"  @Column(name=\"CarRentID\") private Long carRentId;\n"
"  @ManyToOne(fetch=FetchType.LAZY)\n"
"  @JoinColumn(name=\"CustomerID\", nullable=false) private Customer customer;\n"
"  @ManyToOne(fetch=FetchType.LAZY)\n"
"  @JoinColumn(name=\"CarID\", nullable=false) private Car car;\n"
"  @Column(name=\"PickupDate\", nullable=false) private LocalDateTime pickupDate;\n"
"  @Column(name=\"ReturnDate\", nullable=false) private LocalDateTime returnDate;\n"
"  @Column(name=\"RentPrice\", nullable=false, precision=18, scale=2)\n"
"  private BigDecimal rentPrice;\n"
"  @Column(name=\"Status\", nullable=false, length=20) private String status;\n"
"}"
)

h("6.6. Review.java (tham chiếu CarRental)", 2)
code(
"@Entity @Table(name=\"Review\")\n"
"@Getter @Setter @NoArgsConstructor @AllArgsConstructor\n"
"public class Review {\n"
"  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)\n"
"  @Column(name=\"ID\") private Long id;\n"
"  @OneToOne(fetch=FetchType.LAZY, optional=false)\n"
"  @JoinColumn(name=\"CarRentID\", nullable=false, unique=true) private CarRental carRental;\n"
"  @Column(name=\"ReviewStar\", nullable=false) private Integer reviewStar;\n"
"  @Column(name=\"Comment\", nullable=false, length=500) private String comment;\n"
"}"
)
para("Xoá 2 file CarRentalId.java và ReviewId.java. Đổi mọi tham chiếu Integer id "
     "trong Service/Repository/Controller sang Long.", bold=True, color=RGBColor(0xC0,0x00,0x00))
print("section 6 done")

# ===================== 7. REPOSITORY =====================
h("7. Bước 3 - Repository layer", 1)
para("Mỗi entity một interface extends JpaRepository<Entity, Long>. Thêm các query "
     "method cần cho nghiệp vụ. Lưu ý đổi khoá generic từ Integer sang Long.")
code(
"public interface CarRepository extends JpaRepository<Car, Long> {\n"
"  List<Car> findByStatus(String status);\n"
"  List<Car> findByCarNameContainingIgnoreCase(String keyword);\n"
"}\n\n"
"public interface AccountRepository extends JpaRepository<Account, Long> {\n"
"  Optional<Account> findByEmail(String email);\n"
"  boolean existsByEmail(String email);\n"
"}\n\n"
"public interface CustomerRepository extends JpaRepository<Customer, Long> {\n"
"  Optional<Customer> findByAccount_AccountId(Long accountId);\n"
"}\n\n"
"public interface CarRentalRepository extends JpaRepository<CarRental, Long> {\n"
"  List<CarRental> findByCustomer_CustomerId(Long customerId);\n"
"  boolean existsByCar_CarId(Long carId);   // xe đã có giao dịch chưa\n"
"  // báo cáo theo khoảng ngày, sắp xếp giảm dần theo tiền thuê\n"
"  List<CarRental> findByPickupDateBetweenOrderByRentPriceDesc(\n"
"      LocalDateTime start, LocalDateTime end);\n"
"}\n\n"
"public interface ReviewRepository extends JpaRepository<Review, Long> {\n"
"  Optional<Review> findByCarRental_CarRentId(Long carRentId);\n"
"}\n\n"
"public interface CarProducerRepository\n"
"    extends JpaRepository<CarProducer, Long> { }"
)

# ===================== 8. SERVICE =====================
h("8. Bước 4 - Service layer", 1)
para("Service chứa logic nghiệp vụ. Mỗi nghiệp vụ tách interface + impl. Ví dụ "
     "CarService với rule xoá đặc biệt và CarRentalService tính tiền thuê.")
h("8.1. CarServiceImpl - rule xoá xe", 2)
code(
"@Service @RequiredArgsConstructor\n"
"public class CarServiceImpl implements CarService {\n"
"  private final CarRepository carRepo;\n"
"  private final CarRentalRepository rentalRepo;\n"
"\n"
"  public List<Car> findAll() { return carRepo.findAll(); }\n"
"  public Car findById(Long id) { return carRepo.findById(id).orElse(null); }\n"
"  public Car save(Car car) { return carRepo.save(car); }\n"
"\n"
"  // Yêu cầu đề: nếu xe đã nằm trong giao dịch -> chỉ đổi Status,\n"
"  // ngược lại mới xoá hẳn.\n"
"  public void delete(Long id) {\n"
"    if (rentalRepo.existsByCar_CarId(id)) {\n"
"      Car car = carRepo.findById(id).orElseThrow();\n"
"      car.setStatus(\"Inactive\");\n"
"      carRepo.save(car);\n"
"    } else {\n"
"      carRepo.deleteById(id);\n"
"    }\n"
"  }\n"
"}"
)
h("8.2. CarRentalService - tạo giao dịch nhiều xe", 2)
code(
"// Tính tiền = số ngày thuê * giá thuê/ngày, tạo 1 CarRental cho mỗi xe.\n"
"@Transactional\n"
"public void createRental(Long customerId, List<Long> carIds,\n"
"                         LocalDateTime pickup, LocalDateTime ret) {\n"
"  if (!pickup.isBefore(ret))\n"
"      throw new IllegalArgumentException(\"PickupDate phải trước ReturnDate\");\n"
"  Customer customer = customerRepo.findById(customerId).orElseThrow();\n"
"  long days = Math.max(1, Duration.between(pickup, ret).toDays());\n"
"  for (Long carId : carIds) {\n"
"    Car car = carRepo.findById(carId).orElseThrow();\n"
"    CarRental r = new CarRental();\n"
"    r.setCustomer(customer); r.setCar(car);\n"
"    r.setPickupDate(pickup); r.setReturnDate(ret);\n"
"    r.setRentPrice(car.getRentPrice().multiply(BigDecimal.valueOf(days)));\n"
"    r.setStatus(\"Pending\");\n"
"    rentalRepo.save(r);\n"
"  }\n"
"}"
)

# ===================== 9. CONTROLLER =====================
h("9. Bước 5 - Controller & phân quyền", 1)
para("Controller nhận request, gọi Service, đẩy dữ liệu sang view. Dùng tiền tố URL "
     "/admin/** cho chức năng Admin, /customer/** cho Customer để dễ phân quyền ở "
     "SecurityConfig.")
code(
"@Controller @RequiredArgsConstructor\n"
"@RequestMapping(\"/admin/cars\")\n"
"public class CarController {\n"
"  private final CarService carService;\n"
"  private final CarProducerService producerService;\n"
"\n"
"  @GetMapping              // danh sách\n"
"  public String list(Model m){ m.addAttribute(\"cars\", carService.findAll());\n"
"      return \"car/list\"; }\n"
"\n"
"  @GetMapping(\"/new\")      // form thêm\n"
"  public String createForm(Model m){ m.addAttribute(\"car\", new Car());\n"
"      m.addAttribute(\"producers\", producerService.findAll());\n"
"      return \"car/form\"; }\n"
"\n"
"  @PostMapping(\"/save\")    // thêm/sửa\n"
"  public String save(@ModelAttribute Car car){ carService.save(car);\n"
"      return \"redirect:/admin/cars\"; }\n"
"\n"
"  @GetMapping(\"/edit/{id}\")\n"
"  public String editForm(@PathVariable Long id, Model m){\n"
"      m.addAttribute(\"car\", carService.findById(id));\n"
"      m.addAttribute(\"producers\", producerService.findAll());\n"
"      return \"car/form\"; }\n"
"\n"
"  @GetMapping(\"/delete/{id}\")\n"
"  public String delete(@PathVariable Long id){ carService.delete(id);\n"
"      return \"redirect:/admin/cars\"; }\n"
"}"
)
para("Tạo controller tương tự cho Customer, CarProducer, CarRental, Review. "
     "AuthController xử lý /login, /register; HomeController điều hướng trang chủ "
     "theo vai trò.")
print("section 7-9 done")

# ===================== 10. THYMELEAF =====================
h("10. Bước 6 - Giao diện Thymeleaf", 1)
para("Tạo một layout chung rồi các trang con kế thừa. Dùng Thymeleaf Layout Dialect "
     "hoặc th:fragment đơn giản. Ví dụ trang danh sách xe có nút CRUD:")
code(
"<!-- templates/car/list.html -->\n"
"<table border=\"1\">\n"
"  <tr><th>ID</th><th>Tên xe</th><th>Năm</th><th>Giá</th>\n"
"      <th>Trạng thái</th><th>Hãng</th><th>Hành động</th></tr>\n"
"  <tr th:each=\"car : ${cars}\">\n"
"    <td th:text=\"${car.carId}\"></td>\n"
"    <td th:text=\"${car.carName}\"></td>\n"
"    <td th:text=\"${car.carModelYear}\"></td>\n"
"    <td th:text=\"${car.rentPrice}\"></td>\n"
"    <td th:text=\"${car.status}\"></td>\n"
"    <td th:text=\"${car.carProducer.producerName}\"></td>\n"
"    <td>\n"
"      <a th:href=\"@{/admin/cars/edit/{id}(id=${car.carId})}\">Sửa</a>\n"
"      <a th:href=\"@{/admin/cars/delete/{id}(id=${car.carId})}\"\n"
"         onclick=\"return confirm('Xoá xe này?')\">Xoá</a>\n"
"    </td>\n"
"  </tr>\n"
"</table>\n"
"<a th:href=\"@{/admin/cars/new}\">+ Thêm xe</a>"
)
para("Form thêm/sửa dùng chung 1 file, bind bằng th:object:")
code(
"<!-- templates/car/form.html -->\n"
"<form th:action=\"@{/admin/cars/save}\" th:object=\"${car}\" method=\"post\">\n"
"  <input type=\"hidden\" th:field=\"*{carId}\"/>\n"
"  Tên xe: <input th:field=\"*{carName}\"/><br/>\n"
"  Năm:    <input type=\"number\" th:field=\"*{carModelYear}\"/><br/>\n"
"  Màu:    <input th:field=\"*{color}\"/><br/>\n"
"  Sức chứa:<input type=\"number\" th:field=\"*{capacity}\"/><br/>\n"
"  Mô tả:  <input th:field=\"*{description}\"/><br/>\n"
"  Ngày nhập:<input type=\"date\" th:field=\"*{importDate}\"/><br/>\n"
"  Giá thuê:<input type=\"number\" step=\"0.01\" th:field=\"*{rentPrice}\"/><br/>\n"
"  Trạng thái:<input th:field=\"*{status}\"/><br/>\n"
"  Hãng: <select th:field=\"*{carProducer}\">\n"
"    <option th:each=\"p : ${producers}\" th:value=\"${p.producerId}\"\n"
"            th:text=\"${p.producerName}\"></option>\n"
"  </select><br/>\n"
"  <button type=\"submit\">Lưu</button>\n"
"</form>"
)
para("Làm tương tự cho customer, producer, rental, review. Nên thêm CSS ở "
     "static/css để giao diện gọn gàng (điểm trình bày).", italic=True)

# ===================== 11. NGHIEP VU =====================
h("11. Bước 7 - Các chức năng nghiệp vụ chính", 1)
table(
    ["Chức năng", "Vai trò", "URL gợi ý", "Ghi chú triển khai"],
    [
        ["CRUD xe", "Admin", "/admin/cars/**", "Rule xoá đặc biệt (mục 8.1)"],
        ["CRUD khách hàng", "Admin", "/admin/customers/**", "CRUD chuẩn"],
        ["CRUD hãng xe", "Admin", "/admin/producers/**", "CRUD chuẩn"],
        ["Quản lý giao dịch", "Admin", "/admin/rentals/**", "Đổi trạng thái Pending→Renting→Completed"],
        ["Báo cáo thống kê", "Admin", "/admin/reports", "Theo khoảng ngày, sort desc"],
        ["Đăng ký", "Khách", "/register", "Tạo Account(Role=Customer)+Customer"],
        ["Đặt thuê nhiều xe", "Customer", "/customer/rentals/new", "createRental (mục 8.2)"],
        ["Hồ sơ cá nhân", "Customer", "/customer/profile", "Sửa thông tin Customer"],
        ["Lịch sử giao dịch", "Customer", "/customer/rentals", "findByCustomer_CustomerId"],
    ],
    widths=[1.5, 0.9, 1.6, 2.2],
)
para("Luồng đặt thuê nhiều xe: trang /customer/rentals/new hiển thị danh sách xe "
     "Available kèm checkbox; Customer chọn nhiều xe + chọn PickupDate/ReturnDate; "
     "submit gửi danh sách carIds sang controller → gọi createRental.")
print("section 10-11 done")

# ===================== 12. BAO CAO =====================
h("12. Bước 8 - Báo cáo thống kê", 1)
para("Admin nhập StartDate và EndDate, hệ thống liệt kê các giao dịch trong khoảng đó "
     "và sắp xếp giảm dần (theo tiền thuê hoặc theo ngày). Dùng query method đã khai báo "
     "ở Repository.")
code(
"// Service\n"
"public List<CarRental> report(LocalDateTime start, LocalDateTime end){\n"
"  return rentalRepo.findByPickupDateBetweenOrderByRentPriceDesc(start, end);\n"
"}\n\n"
"// Controller\n"
"@GetMapping(\"/admin/reports\")\n"
"public String report(@RequestParam(required=false) String start,\n"
"                     @RequestParam(required=false) String end, Model m){\n"
"  if (start != null && end != null){\n"
"    LocalDateTime s = LocalDate.parse(start).atStartOfDay();\n"
"    LocalDateTime e = LocalDate.parse(end).atTime(23,59,59);\n"
"    m.addAttribute(\"rentals\", reportService.report(s, e));\n"
"    BigDecimal total = ...; // cộng dồn rentPrice\n"
"    m.addAttribute(\"total\", total);\n"
"  }\n"
"  return \"report/index\";\n"
"}"
)
para("Trang report hiển thị form 2 ô ngày + bảng kết quả + tổng doanh thu. Có thể bổ "
     "sung group theo xe/khách để báo cáo đẹp hơn (điểm cộng).", italic=True)

# ===================== 13. SECURITY =====================
h("13. Bước 9 - Security & đăng nhập", 1)
para("Dùng Spring Security để đăng nhập và phân quyền. Mật khẩu nên mã hoá bằng "
     "BCryptPasswordEncoder. Account.Role quyết định quyền truy cập.")
code(
"@Configuration @EnableWebSecurity\n"
"@RequiredArgsConstructor\n"
"public class SecurityConfig {\n"
"  private final UserDetailsService uds;  // load Account theo email\n"
"\n"
"  @Bean PasswordEncoder encoder(){ return new BCryptPasswordEncoder(); }\n"
"\n"
"  @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {\n"
"    http.authorizeHttpRequests(a -> a\n"
"        .requestMatchers(\"/\", \"/register\", \"/login\", \"/css/**\").permitAll()\n"
"        .requestMatchers(\"/admin/**\").hasRole(\"ADMIN\")\n"
"        .requestMatchers(\"/customer/**\").hasRole(\"CUSTOMER\")\n"
"        .anyRequest().authenticated())\n"
"      .formLogin(f -> f.loginPage(\"/login\")\n"
"        .defaultSuccessUrl(\"/\", true).permitAll())\n"
"      .logout(l -> l.logoutSuccessUrl(\"/login?logout\").permitAll());\n"
"    return http.build();\n"
"  }\n"
"}"
)
para("UserDetailsService nạp Account theo email; map Role -> 'ROLE_ADMIN' / "
     "'ROLE_CUSTOMER'. Khi đăng ký, mã hoá password rồi lưu Account(Role='Customer') "
     "kèm một Customer tương ứng.")
para("Lưu ý bảo mật: dữ liệu mẫu để mật khẩu plain text cho dễ test. Khi bật Security "
     "thật, phải hash lại (BCrypt) toàn bộ; nếu không sẽ không đăng nhập được.",
     bold=True, color=RGBColor(0xC0,0x00,0x00))

# ===================== 14. KIEM THU =====================
h("14. Bước 10 - Kiểm thử & chạy thử", 1)
for t in [
    "Build: mvnw clean package (hoặc nút Build trong IDE).",
    "Chạy: mvnw spring-boot:run rồi mở http://localhost:9999.",
    "Đăng nhập Admin: admin@fucar.vn / admin123 (sau khi đã hash lại password trong DB).",
    "Đăng nhập Customer: john@example.com / john123.",
    "Test rule xoá xe: thử xoá xe 'Toyota Vios' (đã có giao dịch) -> phải đổi thành "
    "Inactive, không mất khỏi DB; xoá xe chưa có giao dịch -> mất hẳn.",
    "Test đặt thuê nhiều xe và kiểm tra tiền thuê tính đúng số ngày.",
    "Test báo cáo: nhập khoảng ngày bao trùm dữ liệu mẫu, kiểm tra sắp xếp giảm dần.",
]:
    bullet(t)
para("Mẹo: khi đổi entity mà quên đổi DB (hoặc ngược lại), Hibernate báo lỗi kiểu "
     "'Invalid column name' hoặc 'cannot insert NULL'. Khi đó đối chiếu lại cột giữa "
     "entity và bảng.", italic=True)

# ===================== 15. CHECKLIST =====================
h("15. Checklist nghiệm thu theo rubric", 1)
table(
    ["#", "Hạng mục", "Done?"],
    [
        ["1", "DB CarRentingSystem_DB tạo bằng script, user test/test", "[ ]"],
        ["2", "application.properties trỏ đúng DB, ddl-auto=none", "[ ]"],
        ["3", "6 entity khớp schema v2.0, ID kiểu Long", "[ ]"],
        ["4", "Đủ Repository cho 6 entity", "[ ]"],
        ["5", "Service tách interface + impl (3 lớp)", "[ ]"],
        ["6", "CRUD xe + rule xoá đặc biệt", "[ ]"],
        ["7", "CRUD khách hàng", "[ ]"],
        ["8", "CRUD hãng xe", "[ ]"],
        ["9", "Quản lý giao dịch thuê", "[ ]"],
        ["10", "Đăng ký + đăng nhập + phân quyền", "[ ]"],
        ["11", "Customer đặt thuê nhiều xe", "[ ]"],
        ["12", "Customer xem profile + lịch sử", "[ ]"],
        ["13", "Báo cáo theo khoảng ngày, sort desc", "[ ]"],
        ["14", "Giao diện Thymeleaf đầy đủ form CRUD", "[ ]"],
        ["15", "Đặt tên project đúng: SE2035-JV_<MSSV>_CarRentingSystem", "[ ]"],
    ],
    widths=[0.4, 4.5, 0.8],
)

# ===================== 16. PHU LUC =====================
h("16. Phụ lục - lệnh & mẹo", 1)
para("Các lệnh hay dùng (PowerShell, tại thư mục carrentingsystem):", bold=True)
code(
".\\mvnw clean package          # build\n"
".\\mvnw spring-boot:run        # chạy app\n"
".\\mvnw test                   # chạy unit test\n"
"# chạy script DB:\n"
"sqlcmd -S localhost -U sa -P <pass> -i src\\main\\resources\\db\\CarRentingSystem_DB.sql"
)
para("Thứ tự làm gợi ý: (1) chạy script DB → (2) sửa application.properties → "
     "(3) sửa 6 entity + xoá 2 file *Id → (4) build cho hết lỗi mapping → "
     "(5) làm CRUD từng module → (6) thêm Security → (7) làm nghiệp vụ Customer + "
     "báo cáo → (8) trau chuốt giao diện → (9) test theo checklist.")
para("File đính kèm trong project: db/CarRentingSystem_DB.sql (script + dữ liệu mẫu), "
     "db/README_DATABASE.md (ghi chú DB). Tài liệu này bổ sung phần code còn thiếu.",
     italic=True)

doc.save(OUT)
print("SAVED:", OUT)
