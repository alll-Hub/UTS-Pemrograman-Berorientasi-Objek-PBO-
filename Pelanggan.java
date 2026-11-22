/**
 * Class entitas Pelanggan: pelanggan restoran yang bisa mengumpulkan poin dan menjadi member.
 */
public class Pelanggan {

    private static final int BATAS_POIN_MEMBER = 100;

    private String idPelanggan;
    private String nama;
    private String noTelepon;
    private int poin;
    private boolean member;

    // Konstruktor default
    public Pelanggan() {
        this.idPelanggan = "-";
        this.nama = "Tamu";
        this.noTelepon = "-";
        this.poin = 0;
        this.member = false;
    }

    // Konstruktor berparameter
    public Pelanggan(String idPelanggan, String nama, String noTelepon) {
        this.setIdPelanggan(idPelanggan);
        this.setNama(nama);
        this.setNoTelepon(noTelepon);
        this.poin = 0;
        this.member = false;
    }

    // ===== Getter & Setter (dengan validasi) =====

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pelanggan tidak boleh kosong.");
        }
        this.idPelanggan = idPelanggan.trim().toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pelanggan tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        if (noTelepon == null || !noTelepon.matches("\\d{8,15}")) {
            throw new IllegalArgumentException("No. telepon harus 8-15 digit angka.");
        }
        this.noTelepon = noTelepon;
    }

    public int getPoin() {
        return poin;
    }

    public void setPoin(int poin) {
        if (poin < 0) {
            throw new IllegalArgumentException("Poin tidak boleh negatif.");
        }
        this.poin = poin;
        this.perbaruiStatusMember();
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    // ===== Method perilaku =====

    /** Menambah poin: setiap Rp 10.000 belanja = 1 poin. */
    public void tambahPoin(double belanja) {
        if (belanja > 0) {
            this.poin += (int) (belanja / 10000);
            this.perbaruiStatusMember();
        }
    }

    /** Mengurangi poin (misalnya saat transaksi dibatalkan). Poin tidak akan di bawah 0. */
    public void kurangiPoin(int jumlah) {
        if (jumlah > 0) {
            this.poin = Math.max(0, this.poin - jumlah);
            this.perbaruiStatusMember();
        }
    }

    /** Pelanggan menjadi member bila poin mencapai batas. */
    public void perbaruiStatusMember() {
        this.member = this.poin >= BATAS_POIN_MEMBER;
    }

    public void tampilkanInfo() {
        System.out.printf("%-6s | %-18s | %-14s | poin: %-4d | %s%n",
                idPelanggan, nama, noTelepon, poin, member ? "Member" : "Reguler");
    }
}
