import java.util.ArrayList;

/**
 * Class entitas Pesanan: satu transaksi pemesanan oleh seorang pelanggan.
 * Pesanan memuat minimal 1 menu; pajak 10% dihitung otomatis.
 */
public class Pesanan {

    private static final double TARIF_PAJAK = 0.10;

    private String idPesanan;
    private int nomorMeja;
    private String status; // BARU, SELESAI, DIBATALKAN
    private boolean dibayar;
    private Pelanggan pelanggan;                  // asosiasi
    private ArrayList<Menu> daftarMenu;           // agregasi
    private ArrayList<Integer> daftarJumlah;      // jumlah porsi tiap menu (paralel dengan daftarMenu)

    // Konstruktor default
    public Pesanan() {
        this.idPesanan = "-";
        this.nomorMeja = 1;
        this.status = "BARU";
        this.dibayar = false;
        this.pelanggan = null;
        this.daftarMenu = new ArrayList<>();
        this.daftarJumlah = new ArrayList<>();
    }

    // Konstruktor berparameter
    public Pesanan(String idPesanan, Pelanggan pelanggan, int nomorMeja) {
        this();
        this.setIdPesanan(idPesanan);
        this.setPelanggan(pelanggan);
        this.setNomorMeja(nomorMeja);
    }

    // ===== Getter & Setter (dengan validasi) =====

    public String getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(String idPesanan) {
        if (idPesanan == null || idPesanan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pesanan tidak boleh kosong.");
        }
        this.idPesanan = idPesanan.trim();
    }

    public int getNomorMeja() {
        return nomorMeja;
    }

    public void setNomorMeja(int nomorMeja) {
        if (nomorMeja <= 0) {
            throw new IllegalArgumentException("Nomor meja harus lebih dari 0.");
        }
        this.nomorMeja = nomorMeja;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status tidak boleh null.");
        }
        String s = status.trim().toUpperCase();
        if (!s.equals("BARU") && !s.equals("SELESAI") && !s.equals("DIBATALKAN")) {
            throw new IllegalArgumentException("Status harus BARU, SELESAI, atau DIBATALKAN.");
        }
        this.status = s;
    }

    public boolean isDibayar() {
        return dibayar;
    }

    public void setDibayar(boolean dibayar) {
        this.dibayar = dibayar;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        if (pelanggan == null) {
            throw new IllegalArgumentException("Pesanan harus memiliki pelanggan.");
        }
        this.pelanggan = pelanggan;
    }

    /** Salinan daftar menu agar list internal tidak bisa diubah dari luar. */
    public ArrayList<Menu> getDaftarMenu() {
        return new ArrayList<>(daftarMenu);
    }

    public int getJumlah(int indeks) {
        return daftarJumlah.get(indeks);
    }

    public int jumlahJenisMenu() {
        return daftarMenu.size();
    }

    // ===== Method perilaku =====

    /**
     * Menambah menu ke pesanan. Menu yang sama digabung jumlahnya.
     * false bila menu null, jumlah <= 0, menu habis, atau total jumlah melebihi stok.
     */
    public boolean tambahMenu(Menu menu, int jumlah) {
        if (menu == null || jumlah <= 0 || !menu.isTersedia()) {
            return false;
        }
        int indeks = daftarMenu.indexOf(menu);
        int sudahDipesan = indeks >= 0 ? daftarJumlah.get(indeks) : 0;
        if (sudahDipesan + jumlah > menu.getStok()) {
            return false;
        }
        if (indeks >= 0) {
            daftarJumlah.set(indeks, sudahDipesan + jumlah);
        } else {
            daftarMenu.add(menu);
            daftarJumlah.add(jumlah);
        }
        return true;
    }

    /** Aturan bisnis: pesanan memuat minimal 1 menu. */
    public boolean validasiMinimalSatuMenu() {
        return !daftarMenu.isEmpty();
    }

    public double hitungSubtotal() {
        double subtotal = 0;
        for (int i = 0; i < daftarMenu.size(); i++) {
            subtotal += daftarMenu.get(i).hitungHarga(daftarJumlah.get(i));
        }
        return subtotal;
    }

    /** Pajak 10% dihitung otomatis dari subtotal. */
    public double hitungPajak() {
        return hitungSubtotal() * TARIF_PAJAK;
    }

    public double hitungTotal() {
        return hitungSubtotal() + hitungPajak();
    }

    /** Mengembalikan stok seluruh menu dalam pesanan (dipakai saat pesanan dibatalkan). */
    public void kembalikanStok() {
        for (int i = 0; i < daftarMenu.size(); i++) {
            daftarMenu.get(i).tambahStok(daftarJumlah.get(i));
        }
    }

    public void tampilkanStruk() {
        System.out.println("------------------------------------------------");
        System.out.println("STRUK " + idPesanan + "  | Meja " + nomorMeja + "  | " + status);
        System.out.println("Pelanggan : " + (pelanggan != null ? pelanggan.getNama() : "-"));
        System.out.println("------------------------------------------------");
        for (int i = 0; i < daftarMenu.size(); i++) {
            Menu m = daftarMenu.get(i);
            int qty = daftarJumlah.get(i);
            System.out.printf("%-20s %2d x %12s = %12s%n",
                    m.getNama(), qty, Menu.formatRupiah(m.getHarga()), Menu.formatRupiah(m.hitungHarga(qty)));
        }
        System.out.println("------------------------------------------------");
        System.out.printf("%-32s %15s%n", "Subtotal", Menu.formatRupiah(hitungSubtotal()));
        System.out.printf("%-32s %15s%n", "Pajak 10%", Menu.formatRupiah(hitungPajak()));
        System.out.printf("%-32s %15s%n", "TOTAL", Menu.formatRupiah(hitungTotal()));
        System.out.println("Pembayaran: " + (dibayar ? "LUNAS" : "BELUM DIBAYAR"));
        System.out.println("------------------------------------------------");
    }
}
