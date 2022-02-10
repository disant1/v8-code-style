/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.DATA_ITEM__DATA_PATH;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_FIELD;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.TITLED__TITLE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IDependentProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.dcs.model.core.Presentation;
import com._1c.g5.v8.dt.dcs.model.schema.DataCompositionSchemaDataSetField;
import com._1c.g5.v8.dt.dcs.model.schema.DataSetField;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataPathReferredObject;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.PropertyInfo;
import com._1c.g5.v8.dt.form.service.datasourceinfo.IDataSourceInfoAssociationService;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

public class DynamicListItemTitleCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "form-dynamic-list-item-title"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    private final IDataSourceInfoAssociationService dataSourceInfoAssociationService;

    @Inject
    public DynamicListItemTitleCheck(IV8ProjectManager v8ProjectManager,
        IDataSourceInfoAssociationService dataSourceInfoAssociationService)
    {
        this.v8ProjectManager = v8ProjectManager;
        this.dataSourceInfoAssociationService = dataSourceInfoAssociationService;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Dynamic list field title is empty")
            .description("Dynamic list field title is empty")
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .containment(FORM_FIELD)
            .features(TITLED__TITLE, DATA_ITEM__DATA_PATH);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        FormField field = (FormField)object;
        AbstractDataPath dataPath = field.getDataPath();
        if (dataPath == null || dataPath.getSegments().size() != 2 || dataPath.getObjects().size() != 2)
        {
            return;
        }

        String languageCode = getDefaultLanguageCode(field);
        if (!isTitleEmpty(field.getTitle(), languageCode))
        {
            return;
        }

        Form form = (Form)((IBmObject)field).bmGetTopObject();
        if (!dataSourceInfoAssociationService.isRelatedDynamicList(form, dataPath)
            || !dataSourceInfoAssociationService.isPathResolved(form, dataPath) || monitor.isCanceled())
        {
            return;
        }

        PropertyInfo attribute = dataSourceInfoAssociationService.findPropertyInfo(form, dataPath, 0);
        if (!isCustormQuery(attribute.getSource()) || monitor.isCanceled())
        {
            return;
        }
        DynamicListExtInfo custormQuery = (DynamicListExtInfo)((FormAttribute)attribute.getSource()).getExtInfo();

        String segment = dataPath.getSegments().get(1);
        DataPathReferredObject refObject = dataPath.getObjects().get(1);
        EObject source = refObject.getObject();

        if (isSourceUnkownOrSegmentNotEquals(segment, source)
            && isDcsFieldTitleIsEmpty(custormQuery, segment, languageCode))
        {
            // this is the problem
            resultAceptor.addIssue("Не заполнен заголовок поля динамического списка", TITLED__TITLE);
        }

    }

    private boolean isCustormQuery(Object source)
    {
        return source instanceof FormAttribute && ((FormAttribute)source).getExtInfo() instanceof DynamicListExtInfo
            && ((DynamicListExtInfo)((FormAttribute)source).getExtInfo()).isCustomQuery();
    }

    private String getDefaultLanguageCode(EObject context)
    {
        IV8Project project = v8ProjectManager.getProject(context);
        if (project.getDefaultLanguage() == null && project instanceof IDependentProject)
        {
            return ((IDependentProject)project).getParent().getDefaultLanguage().getLanguageCode();
        }
        else if (project.getDefaultLanguage() != null)
        {
            return project.getDefaultLanguage().getLanguageCode();
        }
        return null;
    }

    private boolean isTitleEmpty(EMap<String, String> title, String languageCode)
    {
        return title == null || languageCode != null && StringUtils.isBlank(title.get(languageCode));
    }

    private boolean isSourceUnkownOrSegmentNotEquals(String segment, EObject source)
    {
        return source == null
            || source instanceof NamedElement && !segment.equalsIgnoreCase(((NamedElement)source).getName());
    }

    private boolean isDcsFieldTitleIsEmpty(DynamicListExtInfo custormQuery, String segment, String languageCode)
    {
        for (DataSetField field : custormQuery.getFields())
        {
            if (field instanceof DataCompositionSchemaDataSetField
                && segment.equalsIgnoreCase(((DataCompositionSchemaDataSetField)field).getDataPath()))
            {
                Presentation title = ((DataCompositionSchemaDataSetField)field).getTitle();
                return title == null || StringUtils.isBlank(title.getValue()) && (title.getLocalValue() == null
                    || isTitleEmpty(title.getLocalValue().getContent(), languageCode));
            }
        }
        return true;
    }
}
