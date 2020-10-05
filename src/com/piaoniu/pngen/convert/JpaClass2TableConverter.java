package com.piaoniu.pngen.convert;

import com.intellij.psi.*;
import com.piaoniu.pngen.model.Column;
import com.piaoniu.pngen.model.Table;

/**
 * @author Hai Chien Teng
 * @date 10/4/2020 2:32 AM
 */
public class JpaClass2TableConverter extends Class2TableConverter {

    static {
        classTypeMap.put("java.lang.Boolean", "TINYINT");
        classTypeMap.put("java.lang.Byte", "SMALLINT");
        classTypeMap.put("java.lang.Character", "SMALLINT");
        classTypeMap.put("java.lang.Double", "DOUBLE");
        classTypeMap.put("java.lang.Float", "FLOAT");
        classTypeMap.put("java.lang.Short", "SMALLINT");
        classTypeMap.put("java.lang.Integer", "INT");
        classTypeMap.put("java.lang.Long", "BIGINT");
    }

    @Override
    @javax.persistence.Column(nullable = true, unique = false, columnDefinition = "")
    public Table convert(PsiClass psiClass) {
        Table table = new Table();

        final PsiAnnotation tableAnnotation = psiClass.getAnnotation(javax.persistence.Table.class.getName());
        ;
        String tableName = null;
        if (tableAnnotation != null) {
            final PsiAnnotationMemberValue value = tableAnnotation.findAttributeValue("value");
            final PsiAnnotationMemberValue name = tableAnnotation.findAttributeValue("name");

            if (!name.getText().equals("\"\"")) {
                tableName = name.getText().replaceAll("\"", "");
            } else if (!value.getText().equals("\"\"")) {
                tableName = value.getText().replaceAll("\"", "");
            }

            if (tableName == null) {
                tableName = psiClass.getName();
            }
        }

        table.setName(tableName);

        for (PsiField psiField : psiClass.getAllFields()) {
            if (psiField.getModifierList().hasExplicitModifier("static")) {
                continue;
            }

            Column column = new Column();
            if (psiField.getDocComment() != null) {
                StringBuilder commentAccum = new StringBuilder();
                for (PsiElement psiElement : psiField.getDocComment().getDescriptionElements()) {
                    commentAccum.append(psiElement.getText());
                }
                column.setComment(commentAccum.toString().replaceAll("\\n+", "").trim());
            }
            final PsiAnnotation columnAnnotation = psiField.getAnnotation(javax.persistence.Column.class.getName());
            String colName = null;
            if (columnAnnotation != null) {

                //
                final Boolean nullable = Boolean.parseBoolean(columnAnnotation.findAttributeValue("nullable").getText());
                final Boolean unique = Boolean.parseBoolean(columnAnnotation.findAttributeValue("unique").getText());
                final String coldef = columnAnnotation.findAttributeValue("columnDefinition").getText();

                if (!coldef.equals("\"\"")) {
                    column.setColumnDefinition(coldef.replaceAll("\"", ""));
                }
                column.setNullable(nullable);
                column.setUnique(unique);

                final PsiAnnotationMemberValue value = columnAnnotation.findAttributeValue("value");
                final PsiAnnotationMemberValue name = columnAnnotation.findAttributeValue("name");

                if (!name.getText().equals("\"\"")) {
                    colName = name.getText().replaceAll("\"", "");
                } else if (!value.getText().equals("\"\"")) {
                    colName = value.getText().replaceAll("\"", "");
                }
            }
            if (colName == null) {
                colName = psiField.getName();
            }
            column.setName(colName);
            if (psiField.getType() instanceof PsiPrimitiveType) {
                column.setType(psiPrimitiveTypeMap.get(psiField.getType()));
            } else if (psiField.getType() instanceof PsiClassType) {
                column.setType(classTypeMap.get(((PsiClassType) psiField.getType()).getCanonicalText()));
            }
            if (column.getType() == null) {
                column.setType(DEFAULT_TYPE);
            }

            final PsiAnnotation idAnnotation = psiField.getAnnotation(javax.persistence.Id.class.getName());
            if (idAnnotation != null) {
                table.setIdName(column.getName());
            }

            table.getColumns().add(column);
        }
        return table;
    }
}
