import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class Main: antarmuka menu (console) untuk sistem restoran.
 */
public class Main {

    private static final Scanner input = new Scanner(System.in);
    private static final Restoran restoran = new Restoran("Warung Nusantara", "Jl. Mawar No. 7, Kendari");

    public static void main(String[] args) {
        isiDataMenu();
        try {
            boolean jalan = true;
            while (jalan) {
                tampilkanMenuUtama();
                String pilihan = baca("Pilih menu: ");
                try {
                    switch (pilihan) {
                        case "1":
                            restoran.tampilkanMenu();
                            break;
                        case "2":
                            tambahMenuBaru();
                            break;
                        case "3":
                            daftarPelangganBaru();
                            break;
                        case "4":
                            restoran.tampilkanPelanggan();
                            break;
                        case "5":
                            buatPesananBaru();
                            break;
                        case "6":
                            restoran.tampilkanRiwayat();
                            break;
                        case "7":
                            batalkanTransaksiTerakhir();
                            break;
                        case "0":
                            jalan = false;
                            break;
                        default:
                            System.out.println("Pilihan tidak valid.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Input ditolak: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\n(input berakhir)");
        }
        System.out.println("Terima kasih. Sampai jumpa!");
    }

    private static void tampilkanMenuUtama() {
        System.out.println();
        System.out.println("===== " + restoran.getNama().toUpperCase() + " =====");
        System.out.println("1. Lihat menu");
        System.out.println("2. Tambah menu baru");
        System.out.println("3. Daftarkan pelanggan");
        System.out.println("4. Lihat pelanggan");
        System.out.println("5. Buat pesanan");
        System.out.println("6. Lihat riwayat transaksi");
        System.out.println("7. Batalkan transaksi terakhir");
        System.out.println("0. Keluar");
    }

    /** Hanya menu yang diisi otomatis. Data pelanggan (termasuk ID) diinput sendiri. */
    private static void isiDataMenu() {
        restoran.tambahMenu(new Menu("M01", "Nasi Goreng", 25000, 10));
        restoran.tambahMenu(new Menu("M02", "Ayam Bakar", 32000, 8));
        restoran.tambahMenu(new Menu("M03", "Es Teh Manis", 6000, 20));
        restoran.tambahMenu(new Menu("M04", "Jus Alpukat", 15000, 2));
    }

    private static void tambahMenuBaru() {
        String kode = baca("Kode menu   : ");
        String nama = baca("Nama menu   : ");
        double harga = bacaDouble("Harga       : ");
        int stok = bacaInt("Stok        : ");
        Menu menu = new Menu(kode, nama, harga, stok);
        System.out.println(restoran.tambahMenu(menu) ? "Menu ditambahkan." : "Gagal: kode menu sudah dipakai.");
    }

    private static void daftarPelangganBaru() {
        String id = baca("ID pelanggan : ");
        String nama = baca("Nama         : ");
        String telp = baca("No. telepon  : ");
        Pelanggan p = new Pelanggan(id, nama, telp);
        System.out.println(restoran.daftarkanPelanggan(p) ? "Pelanggan terdaftar." : "Gagal: ID sudah dipakai.");
    }

    private static void buatPesananBaru() {
        if (restoran.jumlahMenu() == 0) {
            System.out.println("Belum ada menu. Tambahkan menu dulu lewat pilihan 2.");
            return;
        }
        restoran.tampilkanPelanggan();
        Pelanggan pelanggan = restoran.cariPelanggan(baca("ID pelanggan : "));
        if (pelanggan == null) {
            String daftar = baca("Pelanggan tidak ditemukan. Daftarkan sekarang? (y/n): ");
            if (!daftar.equalsIgnoreCase("y")) {
                return;
            }
            daftarPelangganBaru();
            pelanggan = restoran.cariPelanggan(baca("ID pelanggan yang baru didaftarkan : "));
            if (pelanggan == null) {
                System.out.println("Pelanggan tidak ditemukan, pesanan dibatalkan.");
                return;
            }
        }
        int meja = bacaInt("Nomor meja   : ");
        Pesanan pesanan = restoran.buatPesanan(pelanggan, meja);
        if (pesanan == null) {
            System.out.println("Pesanan tidak bisa dibuat (cek nomor meja / restoran tutup).");
            return;
        }

        restoran.tampilkanMenu();
        System.out.println("Ketik kode menu untuk menambah, atau ketik SELESAI untuk lanjut.");
        while (true) {
            String kode = baca("Kode menu : ");
            if (kode.equalsIgnoreCase("SELESAI")) {
                break;
            }
            Menu menu = restoran.cariMenu(kode);
            if (menu == null) {
                System.out.println("Menu tidak ditemukan.");
                continue;
            }
            int jumlah = bacaInt("Jumlah    : ");
            if (pesanan.tambahMenu(menu, jumlah)) {
                System.out.println("  + " + jumlah + " x " + menu.getNama());
            } else {
                System.out.println("  Gagal: jumlah tidak valid, menu habis, atau stok tidak cukup.");
            }
        }

        if (!pesanan.validasiMinimalSatuMenu()) {
            System.out.println("Pesanan dibatalkan: harus memuat minimal 1 menu.");
            return;
        }
        pesanan.tampilkanStruk();
        String bayar = baca("Bayar sekarang? (y/n): ");
        if (bayar.equalsIgnoreCase("y")) {
            if (restoran.selesaikanPesanan(pesanan)) {
                System.out.println("Pembayaran berhasil. Struk akhir:");
                pesanan.tampilkanStruk();
            } else {
                System.out.println("Pesanan gagal diselesaikan.");
            }
        } else {
            System.out.println("Pesanan tidak disimpan.");
        }
    }

    private static void batalkanTransaksiTerakhir() {
        Pesanan batal = restoran.batalkanPesananTerakhir();
        if (batal == null) {
            System.out.println("Riwayat kosong, tidak ada transaksi yang bisa dibatalkan.");
        } else {
            System.out.println("Transaksi " + batal.getIdPesanan() + " dibatalkan, stok dikembalikan.");
        }
    }

    // ===== Helper input =====

    private static String baca(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private static int bacaInt(String prompt) {
        try {
            return Integer.parseInt(baca(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Harus berupa bilangan bulat.");
        }
    }

    private static double bacaDouble(String prompt) {
        try {
            return Double.parseDouble(baca(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Harus berupa angka.");
        }
    }
}
