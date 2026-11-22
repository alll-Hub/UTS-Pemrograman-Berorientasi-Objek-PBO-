/**
 * ADT Stack (LIFO) berbasis array untuk menyimpan riwayat pesanan yang sudah selesai.
 * Dipakai untuk fitur "batalkan transaksi terakhir".
 *
 * Atribut internal private; interaksi hanya lewat operasi publik.
 * Kondisi penuh/kosong ditangani tanpa exception: push() mengembalikan false saat penuh,
 * pop() dan peek() mengembalikan null saat kosong.
 */
public class StackPesanan {

    private static final int KAPASITAS_DEFAULT = 5;

    private Pesanan[] data;
    private int top;
    private int kapasitas;

    public StackPesanan() {
        this(KAPASITAS_DEFAULT);
    }

    public StackPesanan(int kapasitas) {
        this.kapasitas = kapasitas > 0 ? kapasitas : KAPASITAS_DEFAULT;
        this.data = new Pesanan[this.kapasitas];
        this.top = -1;
    }

    /** Menambah pesanan ke puncak stack. false bila stack penuh atau pesanan null. */
    public boolean push(Pesanan pesanan) {
        if (pesanan == null || isFull()) {
            return false;
        }
        data[++top] = pesanan;
        return true;
    }

    /** Mengambil dan menghapus pesanan di puncak. null bila stack kosong. */
    public Pesanan pop() {
        if (isEmpty()) {
            return null;
        }
        Pesanan hasil = data[top];
        data[top--] = null;
        return hasil;
    }

    /** Melihat pesanan di puncak tanpa menghapus. null bila stack kosong. */
    public Pesanan peek() {
        if (isEmpty()) {
            return null;
        }
        return data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == kapasitas - 1;
    }

    public int size() {
        return top + 1;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    /** Mengosongkan seluruh stack (arsip riwayat lama). */
    public void kosongkan() {
        while (!isEmpty()) {
            pop();
        }
    }
}
