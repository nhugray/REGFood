package com.finalterm.regfood.local.repository;

import android.content.Context;

import com.finalterm.regfood.local.AppDatabase;
import com.finalterm.regfood.local.RoomDatabaseProvider;
import com.finalterm.regfood.local.entity.FoodAliasEntity;
import com.finalterm.regfood.local.entity.FoodItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodCatalogSeeder {

    public interface Callback {
        void onFinished();
    }

    private final Context appContext;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FoodCatalogSeeder(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void seedIfNeeded(Callback callback) {
        executor.execute(() -> {
            AppDatabase db = RoomDatabaseProvider.getInstance(appContext);
            if (db.foodDao().getActiveFoods().isEmpty()) {
                seedFoods(db);
            }
            if (callback != null) {
                callback.onFinished();
            }
        });
    }

    private void seedFoods(AppDatabase db) {
        long now = System.currentTimeMillis();
        List<FoodSeed> seeds = new ArrayList<>();
        seeds.add(new FoodSeed("BANH_BE_O_01", "Bánh bèo", "banh beo", "banh beo", "Bánh", "1 đĩa (10 cái)", 195, "Bánh bèo tôm chấy truyền thống Huế", "banh_beo.jpg"));
        seeds.add(new FoodSeed("BANH_BOT_LOC_01", "Bánh bột lọc", "banh bot loc", "banh bot loc", "Bánh", "1 phần (8 cái)", 210, "Bánh bột lọc nhân tôm thịt", "banh_bot_loc.jpg"));
        seeds.add(new FoodSeed("BANH_CAN_01", "Bánh căn", "banh can", "banh can", "Bánh", "1 phần (6 cái)", 280, "Bánh căn trứng cút Phan Rang", "banh_can.jpg"));
        seeds.add(new FoodSeed("BANH_CANH_01", "Bánh canh", "banh canh", "banh canh", "Bánh canh", "1 tô", 480, "Bánh canh giò heo nước cốt dừa", "banh_canh.jpg"));
        seeds.add(new FoodSeed("BANH_CHUNG_01", "Bánh chưng", "banh chung", "banh chung", "Bánh", "1 miếng (200g)", 420, "Bánh chưng nhân đậu thịt truyền thống", "banh_chung.jpg"));
        seeds.add(new FoodSeed("BANH_CUON_01", "Bánh cuốn", "banh cuon", "banh cuon", "Bánh", "1 phần", 310, "Bánh cuốn nhân thịt hành phi", "banh_cuon.jpg"));
        seeds.add(new FoodSeed("BANH_DUC_01", "Bánh đúc", "banh duc", "banh duc", "Bánh", "1 phần (150g)", 190, "Bánh đúc mặn nhân tôm thịt", "banh_duc.jpg"));
        seeds.add(new FoodSeed("BANH_GIO_01", "Bánh giò", "banh gio", "banh gio", "Bánh", "1 cái (150g)", 310, "Bánh giò nhân thịt mộc nhĩ", "banh_gio.jpg"));
        seeds.add(new FoodSeed("BANH_KHOT_01", "Bánh khọt", "banh khot", "banh khot", "Bánh", "1 phần (8 cái)", 340, "Bánh khọt tôm dừa Vũng Tàu", "banh_khot.jpg"));
        seeds.add(new FoodSeed("BANH_MI_01", "Bánh mì", "banh mi", "banh mi", "Bánh mì", "1 ổ (100g)", 380, "Bánh mì thịt nguội pate truyền thống", "banh_mi.jpg"));
        seeds.add(new FoodSeed("BANH_PIA_01", "Bánh pía", "banh pia", "banh pia", "Bánh", "1 cái (80g)", 295, "Bánh pía sầu riêng đậu xanh", "banh_pia.jpg"));
        seeds.add(new FoodSeed("BANH_TET_01", "Bánh tét", "banh tet", "banh tet", "Bánh", "1 khoanh (200g)", 400, "Bánh tét nhân chuối / nhân mặn", "banh_tet.jpg"));
        seeds.add(new FoodSeed("BANH_TRANG_NUONG_01", "Bánh tráng nướng", "banh trang nuong", "banh trang nuong", "Bánh", "1 cái", 320, "Bánh tráng nướng trứng hành mỡ", "banh_trang_nuong.jpg"));
        seeds.add(new FoodSeed("BANH_XEO_01", "Bánh xèo", "banh xeo", "banh xeo", "Bánh", "1 cái (200g)", 490, "Bánh xèo tôm thịt giá đỗ", "banh_xeo.jpg"));
        seeds.add(new FoodSeed("BUN_BO_HUE_01", "Bún bò Huế", "bun bo hue", "bun bo hue", "Bún", "1 tô", 520, "Bún bò Huế chả cua giò heo", "bun_bo_hue.jpg"));
        seeds.add(new FoodSeed("BUN_DAU_MAM_TOM_01", "Bún đậu mắm tôm", "bun dau mam tom", "bun dau mam tom", "Bún", "1 phần", 560, "Bún đậu phụ chiên mắm tôm", "bun_dau_mam_tom.jpg"));
        seeds.add(new FoodSeed("BUN_MAM_01", "Bún mắm", "bun mam", "bun mam", "Bún", "1 tô", 580, "Bún mắm hải sản thịt quay", "bun_mam.jpg"));
        seeds.add(new FoodSeed("BUN_RIEU_01", "Bún riêu", "bun rieu", "bun rieu", "Bún", "1 tô", 430, "Bún riêu cua đồng cà chua", "bun_rieu.jpg"));
        seeds.add(new FoodSeed("BUN_THIT_NUONG_01", "Bún thịt nướng", "bun thit nuong", "bun thit nuong", "Bún", "1 phần", 510, "Bún thịt nướng chả giò rau sống", "bun_thit_nuong.jpg"));
        seeds.add(new FoodSeed("CA_KHO_TO_01", "Cá kho tộ", "ca kho to", "ca kho to", "Món mặn", "1 phần + cơm", 470, "Cá kho tộ nước dừa tiêu đen", "ca_kho_to.jpg"));
        seeds.add(new FoodSeed("CANH_CHUA_01", "Canh chua", "canh chua", "canh chua", "Canh", "1 tô", 180, "Canh chua cá lóc me dứa", "canh_chua.jpg"));
        seeds.add(new FoodSeed("CAO_LAU_01", "Cao lầu", "cao lau", "cao lau", "Mì", "1 tô", 490, "Cao lầu Hội An xá xíu rau thơm", "cao_lau.jpg"));
        seeds.add(new FoodSeed("CHAO_LONG_01", "Cháo lòng", "chao long", "chao long", "Cháo", "1 tô", 390, "Cháo lòng huyết heo gừng hành", "chao_long.jpg"));
        seeds.add(new FoodSeed("COM_TAM_01", "Cơm tấm", "com tam", "com tam", "Cơm", "1 phần", 690, "Cơm tấm sườn bì chả trứng", "com_tam.jpg"));
        seeds.add(new FoodSeed("GOI_CUON_01", "Gỏi cuốn", "goi cuon", "goi cuon", "Cuốn", "1 cuốn (80g)", 120, "Gỏi cuốn tôm thịt rau sốt đậu phộng", "goi_cuon.jpg"));
        seeds.add(new FoodSeed("HU_TIEU_01", "Hủ tiếu", "hu tieu", "hu tieu", "Hủ tiếu", "1 tô", 520, "Hủ tiếu Nam Vang thịt xá xíu tôm", "hu_tieu.jpg"));
        seeds.add(new FoodSeed("MI_QUANG_01", "Mì Quảng", "mi quang", "mi quang", "Mì", "1 tô", 550, "Mì Quảng tôm thịt bánh tráng mè", "mi_quang.jpg"));
        seeds.add(new FoodSeed("NEM_CHUA_01", "Nem chua", "nem chua", "nem chua", "Ăn vặt", "1 cái (40g)", 85, "Nem chua tỏi ớt lên men truyền thống", "nem_chua.jpg"));
        seeds.add(new FoodSeed("PHO_01", "Phở", "pho", "pho", "Phở", "1 tô", 520, "Phở bò truyền thống rau thơm giá", "pho.jpg"));
        seeds.add(new FoodSeed("XOI_XEO_01", "Xôi xéo", "xoi xeo", "xoi xeo", "Xôi", "1 phần (200g)", 480, "Xôi xéo đậu xanh hành phi mỡ", "xoi_xeo.jpg"));

        List<FoodItemEntity> foods = new ArrayList<>();
        for (FoodSeed seed : seeds) {
            foods.add(seed.toEntity(now));
        }

        List<Long> ids = new ArrayList<>();
        for (FoodItemEntity food : foods) {
            ids.add(db.foodDao().insertFood(food));
        }

        List<FoodAliasEntity> aliases = new ArrayList<>();
        for (int i = 0; i < seeds.size(); i++) {
            FoodSeed seed = seeds.get(i);
            long foodId = ids.get(i);
            aliases.add(new FoodAliasEntity(foodId, seed.normalized, "name", now));
            aliases.add(new FoodAliasEntity(foodId, seed.name.toLowerCase(Locale.ROOT), "name", now));
        }
        db.foodDao().insertAliases(aliases);
    }

    private static final class FoodSeed {
        final String code;
        final String name;
        final String normalized;
        final String aiLabel;
        final String category;
        final String serving;
        final double kcal;
        final String description;
        final String imageName;

        FoodSeed(String code, String name, String normalized, String aiLabel, String category, String serving,
                 double kcal, String description, String imageName) {
            this.code = code;
            this.name = name;
            this.normalized = normalized;
            this.aiLabel = aiLabel;
            this.category = category;
            this.serving = serving;
            this.kcal = kcal;
            this.description = description;
            this.imageName = imageName;
        }

        FoodItemEntity toEntity(long now) {
            return new FoodItemEntity(code, name, normalized, aiLabel, category, serving, kcal,
                    0, 0, 0, 0, 0, 0, 0, serving, description, imageName, true, now, now);
        }
    }
}
