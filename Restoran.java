import java.util.ArrayList;

/**
 * Class entitas Restoran: mengelola menu, pelanggan, pembuatan pesanan,
 * dan riwayat transaksi (ADT StackPesanan).
 */
public class Restoran {

    private String nama;
    private String alamat;
    private double totalPendapatan;
    private int jumlahPesanan;
    private boolean buka;
    private ArrayList<Menu> daftarMenu;             // komposisi
    private ArrayList<Pelanggan> daftarPelanggan;   // agregasi
    private StackPesanan riwayat;                   // komposisi (ADT)

    // Konstruktor default
    public Restoran() {
        this.nama = "Restoran";
        this.alamat = "-";
        this.totalPendapatan = 0;
        this.jumlahPesanan = 0;
        this.buka = true;
        this.daftarMenu = new ArrayList<>();
        this.daftarPelanggan = new ArrayList<>();
        this.riwayat = new StackPesanan();
    }

    // Konstruktor berparameter
    public Restoran(String nama, String alamat) {
        this();
        this.setNama(nama);
        this.setAlamat(alamat);
    }

    // ===== Getter & Setter (dengan validasi) =====

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama restoran tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        if (alamat == null || alamat.trim().isEmpty()) {
            throw new IllegalArgumentException("Alamat tidak boleh kosong.");
        }
        this.alamat = alamat.trim();
    }

    public double getTotalPendapatan() {
        return totalPendapatan;
    }

    public void setTotalPendapatan(double totalPendapatan) {
        if (totalPendapatan < 0) {
            throw new IllegalArgumentException("Total pendapatan tidak boleh negatif.");
        }
        this.totalPendapatan = totalPendapatan;
    }

    public int getJumlahPesanan() {
        return jumlahPesanan;
    }

    public void setJumlahPesanan(int jumlahPesanan) {
        if (jumlahPesanan < 0) {
            throw new IllegalArgumentException("Jumlah pesanan tidak boleh negatif.");
        }
        this.jumlahPesanan = jumlahPesanan;
    }

    public boolean isBuka() {
        return buka;
    }

    public void setBuka(boolean buka) {
        this.buka = buka;
    }

    public int jumlahMenu() {
        return daftarMenu.size();
    }

    // ===== Method perilaku =====

    public boolean tambahMenu(Menu menu) {
        if (menu == null || cariMenu(menu.getKode()) != null) {
            return false; // null atau kode sudah dipakai
        }
        daftarMenu.add(menu);
        return true;
    }

    public boolean daftarkanPelanggan(Pelanggan pelanggan) {
        if (pelanggan == null || cariPelanggan(pelanggan.getIdPelanggan()) != null) {
            return false;
        }
        daftarPelanggan.add(pelanggan);
        return true;
    }

    public Menu cariMenu(String kode) {
        if (kode == null) {
            return null;
        }
        for (Menu m : daftarMenu) {
            if (m.getKode().equalsIgnoreCase(kode.trim())) {
                return m;
            }
        }
        return null;
    }

    public Pelanggan cariPelanggan(String id) {
        if (id == null) {
            return null;
        }
        for (Pelanggan p : daftarPelanggan) {
            if (p.getIdPelanggan().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    /** Membuat pesanan baru (belum disimpan ke riwayat sampai diselesaikan). */
    public Pesanan buatPesanan(Pelanggan pelanggan, int nomorMeja) {
        if (!buka || pelanggan == null || nomorMeja <= 0) {
            return null;
        }
        String id = String.format("PSN-%03d", jumlahPesanan + 1);
        return new Pesanan(id, pelanggan, nomorMeja);
    }

    /**
     * Menyelesaikan & membayar pesanan: validasi minimal 1 menu, cek stok, kurangi stok,
     * tambah pendapatan dan poin pelanggan, lalu simpan ke stack riwayat.
     */
    public boolean selesaikanPesanan(Pesanan pesanan) {
        if (pesanan == null || !pesanan.getStatus().equals("BARU")) {
            return false;
        }
        if (!pesanan.validasiMinimalSatuMenu()) {
            System.out.println("Pesanan harus memuat minimal 1 menu.");
            return false;
        }
        ArrayList<Menu> menus = pesanan.getDaftarMenu();
        for (int i = 0; i < menus.size(); i++) {
            if (menus.get(i).getStok() < pesanan.getJumlah(i)) {
                System.out.println("Stok " + menus.get(i).getNama() + " tidak mencukupi.");
                return false;
            }
        }
        if (riwayat.isFull()) {
            System.out.println("(Riwayat penuh, riwayat lama diarsipkan.)");
            riwayat.kosongkan();
        }
        for (int i = 0; i < menus.size(); i++) {
            menus.get(i).kurangiStok(pesanan.getJumlah(i));
        }
        pesanan.setDibayar(true);
        pesanan.setStatus("SELESAI");
        totalPendapatan += pesanan.hitungTotal();
        jumlahPesanan++;
        pesanan.getPelanggan().tambahPoin(pesanan.hitungTotal());
        return riwayat.push(pesanan);
    }

    /** Membatalkan transaksi terakhir (LIFO). null bila riwayat kosong. */
    public Pesanan batalkanPesananTerakhir() {
        Pesanan terakhir = riwayat.pop();
        if (terakhir == null) {
            return null;
        }
        terakhir.kembalikanStok();
        terakhir.setStatus("DIBATALKAN");
        terakhir.setDibayar(false);
        totalPendapatan = Math.max(0, totalPendapatan - terakhir.hitungTotal());
        terakhir.getPelanggan().kurangiPoin((int) (terakhir.hitungTotal() / 10000));
        return terakhir;
    }

    public void tampilkanMenu() {
        System.out.println("=== MENU " + nama.toUpperCase() + " ===");
        if (daftarMenu.isEmpty()) {
            System.out.println("(belum ada menu)");
        }
        for (Menu m : daftarMenu) {
            m.tampilkanInfo();
        }
    }

    public void tampilkanPelanggan() {
        System.out.println("=== DAFTAR PELANGGAN ===");
        if (daftarPelanggan.isEmpty()) {
            System.out.println("(belum ada pelanggan)");
        }
        for (Pelanggan p : daftarPelanggan) {
            p.tampilkanInfo();
        }
    }

    public void tampilkanRiwayat() {
        System.out.println("=== RIWAYAT TRANSAKSI ===");
        System.out.println("Terisi : " + riwayat.size() + "/" + riwayat.getKapasitas()
                + (riwayat.isEmpty() ? " (kosong)" : "") + (riwayat.isFull() ? " (penuh)" : ""));
        Pesanan terakhir = riwayat.peek();
        if (terakhir != null) {
            System.out.println("Transaksi terakhir:");
            terakhir.tampilkanStruk();
        }
        System.out.printf("Total pendapatan: %s dari %d pesanan diproses%n",
                Menu.formatRupiah(totalPendapatan), jumlahPesanan);
    }
}
