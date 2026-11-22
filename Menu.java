import java.util.Locale;

/**
 * Class entitas Menu: satu jenis hidangan/minuman yang dijual restoran.
 */
public class Menu {

    private String kode;
    private String nama;
    private double harga;
    private int stok;
    private boolean tersedia;

    // Konstruktor default
    public Menu() {
        this.kode = "-";
        this.nama = "Tanpa nama";
        this.harga = 0;
        this.stok = 0;
        this.tersedia = false;
    }

    // Konstruktor berparameter
    public Menu(String kode, String nama, double harga, int stok) {
        this.setKode(kode);
        this.setNama(nama);
        this.setHarga(harga);
        this.setStok(stok);
    }

    // ===== Getter & Setter (dengan validasi) =====

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode menu tidak boleh kosong.");
        }
        this.kode = kode.trim().toUpperCase();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama menu tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga menu harus lebih dari 0.");
        }
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif.");
        }
        this.stok = stok;
        this.tersedia = stok > 0;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        // menu tanpa stok tidak boleh ditandai tersedia
        this.tersedia = tersedia && this.stok > 0;
    }

    // ===== Method perilaku =====

    /** Mengurangi stok. Mengembalikan false bila jumlah tidak valid / stok tidak cukup. */
    public boolean kurangiStok(int jumlah) {
        if (jumlah <= 0 || jumlah > this.stok) {
            return false;
        }
        this.stok -= jumlah;
        this.tersedia = this.stok > 0;
        return true;
    }

    /** Menambah stok (misalnya saat pesanan dibatalkan atau restock). */
    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
            this.tersedia = true;
        }
    }

    /** Menghitung harga untuk sejumlah porsi. */
    public double hitungHarga(int jumlah) {
        return this.harga * jumlah;
    }

    public void tampilkanInfo() {
        System.out.printf("%-6s | %-20s | %12s | stok: %-3d | %s%n",
                kode, nama, formatRupiah(harga), stok, tersedia ? "Tersedia" : "Habis");
    }

    /** Helper format rupiah, mis. Rp 25.000 */
    public static String formatRupiah(double nilai) {
        return "Rp " + String.format(Locale.forLanguageTag("id-ID"), "%,.0f", nilai);
    }
}
