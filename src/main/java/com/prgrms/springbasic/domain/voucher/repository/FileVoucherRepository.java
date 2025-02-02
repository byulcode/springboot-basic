package com.prgrms.springbasic.domain.voucher.repository;

import com.prgrms.springbasic.domain.voucher.entity.DiscountType;
import com.prgrms.springbasic.domain.voucher.entity.Voucher;
import com.prgrms.springbasic.util.CsvFileUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("ignore")
public class FileVoucherRepository implements VoucherRepository {
    private Map<UUID, Voucher> vouchers = new ConcurrentHashMap<>();

    @Value("${repository.voucher.filePath}")
    private String filePath;

    @PostConstruct
    public void init() {
        vouchers = readVoucherFromFile(filePath);
    }

    @Override
    public Voucher saveVoucher(Voucher voucher) {
        System.out.println("dho");
        vouchers.put(voucher.getVoucherId(), voucher);
        String fieldToCsvString = String.format("%s,%s,%s", voucher.getVoucherId(), voucher.getDiscountType(), voucher.getDiscountValue());
        CsvFileUtil.addObjectToFile(filePath, fieldToCsvString);
        return voucher;
    }

    @Override
    public List<Voucher> findAll() {
        return vouchers.values().stream()
                .toList();
    }

    @Override
    public void updateVoucher(Voucher voucher) {
        vouchers.put(voucher.getVoucherId(), voucher);
        List<String> itemList = new ArrayList<>();
        for (Voucher v : vouchers.values()) {
            itemList.add(String.format("%s,%s,%s", v.getVoucherId(), v.getDiscountType(), v.getDiscountValue()));
        }
        CsvFileUtil.writeItemsToFile(filePath, itemList);
    }

    @Override
    public void deleteAll() {
        vouchers.clear();
        CsvFileUtil.deleteAllFileContent(filePath);
    }

    @Override
    public void deleteById(UUID voucherId) {

    }

    @Override
    public Optional<Voucher> findVoucherById(UUID voucherId) {
        return Optional.ofNullable(vouchers.get(voucherId));
    }

    @Override
    public List<Voucher> findByCreatedAt(LocalDate date) {
        return null;
    }

    @Override
    public List<Voucher> findByDiscountType(DiscountType discountType) {
        return null;
    }

    @Override
    public List<Voucher> findByCreatedAtAndDiscountType(LocalDate date, DiscountType discountType) {
        return null;
    }

    private static Map<UUID, Voucher> readVoucherFromFile(String filePath) {
        return CsvFileUtil.readItemsFromFile(filePath, parts -> {
            UUID voucherId = UUID.fromString(parts[0]);
            String discountType = parts[1];
            long discountValue = Long.parseLong(parts[2]);
            LocalDateTime createdAt = LocalDateTime.parse(parts[3]);
            return Voucher.createVoucher(voucherId, discountType, discountValue, createdAt);
        });
    }
}
