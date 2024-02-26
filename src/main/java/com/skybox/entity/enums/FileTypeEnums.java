package com.skybox.entity.enums;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: FileTypeEnums
 * @Datetime: 2023/11/11 18:20
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示文件的类型
 */

public enum FileTypeEnums {
    //1:视频 2:音频  3:图片 4:pdf 5:word 6:excel 7:txt 8:code 9:zip 10:其他文件
    VIDEO(FileCategoryEnums.VIDEO, 1, new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"}, "视频"),
    MUSIC(FileCategoryEnums.MUSIC, 2, new String[]{".mp3", ".wav", ".wma", ".mp2", ".flac", ".midi", ".ra", ".ape", ".aac", ".cda"}, "音频"),
    IMAGE(FileCategoryEnums.IMAGE, 3, new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".dds", ".psd", ".pdt", ".webp", ".xmp", ".svg", ".tiff"}, "图片"),
    PDF(FileCategoryEnums.DOC, 4, new String[]{".pdf", ".pptx"}, "pdf"),
    WORD(FileCategoryEnums.DOC, 5, new String[]{".docx"}, "word"),
    EXCEL(FileCategoryEnums.DOC, 6, new String[]{".xlsx"}, "excel"),
    TXT(FileCategoryEnums.DOC, 7, new String[]{".txt"}, "txt文本"),
    PROGRAME(FileCategoryEnums.OTHERS, 8, new String[]{".h", ".c", ".hpp", ".hxx", ".cpp", ".cc", ".c++", ".cxx", ".m", ".o", ".s", ".dll", ".cs", ".java", ".class", ".js", ".ts", ".css", ".scss", ".vue", ".jsx", ".sql", ".md", ".json", ".html", ".xml"}, "CODE"),
    ZIP(FileCategoryEnums.OTHERS, 9, new String[]{"rar", ".zip", ".7z", ".cab", ".arj", ".lzh", ".tar", ".gz", ".ace", ".uue", ".bz", ".jar", ".iso", ".mpq"}, "压缩包"),
    OTHERS(FileCategoryEnums.OTHERS, 10, new String[]{}, "其他");

    private final FileCategoryEnums category; // 文件所属的类别
    private final Integer type;   // 文件类型的编号
    private final String[] suffixs;   // 文件的后缀名数组，用于匹配文件类型
    private final String desc;    // 文件类型的描述

    FileTypeEnums(FileCategoryEnums category, Integer type, String[] suffixs, String desc) {
        this.category = category;
        this.type = type;
        this.suffixs = suffixs;
        this.desc = desc;
    }

    /**
     * @param suffix
     * @return FileTypeEnums
     * @description 根据文件后缀名获取对应的文件类型枚举常量
     */
    public static FileTypeEnums getFileTypeBySuffix(String suffix) {
        // 遍历所有枚举常量的后缀名数组，如果匹配到指定后缀名，则返回对应的文件类型枚举常量
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (ArrayUtils.contains(item.getSuffixs(), suffix)) {
                return item;
            }
        }
        // 如果没有匹配到，则返回FileTypeEnums.OTHERS，表示其他文件类型
        return FileTypeEnums.OTHERS;
    }

    /**
     * @param type
     * @return FileTypeEnums
     * @description 根据文件类型的编号获取对应的文件类型枚举常量
     */
    public static FileTypeEnums getByType(Integer type) {
        // 遍历所有枚举常量，如果找到与指定类型编号相匹配的枚举常量，则返回该枚举常量
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        // 如果没有找到，则返回null
        return null;
    }

    public String[] getSuffixs() {
        return suffixs;
    }

    public FileCategoryEnums getCategory() {
        return category;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}

