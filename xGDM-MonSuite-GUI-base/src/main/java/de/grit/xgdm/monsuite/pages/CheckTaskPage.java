/*-
 * #%L
 * xGDM-MonSuite GUI (Base)
 * %%
 * Copyright (C) 2022 - 2025 grit GmbH
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package de.grit.xgdm.monsuite.pages;

import static de.grit.vaadin.common.Messages.get;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.grit.vaadin.GritTheme;
import de.grit.vaadin.common.FontAwesome;
import de.grit.vaadin.common.Messages;
import de.grit.vaadin.common.annotation.MenuButton;
import de.grit.vaadin.common.annotation.Navigation;
import de.grit.vaadin.common.components.DualPasswordField;
import de.grit.vaadin.common.components.FilteredView;
import de.grit.vaadin.common.components.GritView;
import de.grit.vaadin.common.interfaces.IRefreshable;
import de.grit.xgdm.monsuite.UIUtils;
import de.grit.xgdm.monsuite.data.Actions;
import de.grit.xgdm.monsuite.data.CheckTask;
import de.grit.xgdm.monsuite.data.MessageType;
import de.grit.xgdm.monsuite.data.Notification;
import de.grit.xgdm.monsuite.data.Person;
import de.grit.xgdm.monsuite.data.Sensor;
import de.grit.xgdm.monsuite.data.SensorConfig;
import de.grit.xgdm.monsuite.data.SensorTyp;
import io.ebean.Ebean;

@Navigation(path = "tasks", titleKey = "txt.root.tab.checktask", menu = @MenuButton(icon = FontAwesome.BOOK, sort = 400))
public class CheckTaskPage extends GritView implements Button.ClickListener, IRefreshable {

	private static final long serialVersionUID = 5384107483623214161L;

	private static final Logger LOG = LoggerFactory.getLogger(MessagePage.class);

	private static final String DATE_FMT_STRING = get("txt.date.format");

	private static final String DATE_FMT_SHORT_STRING = get("txt.date.format.short");

	private static final DateFormat DATE_FMT = new SimpleDateFormat(DATE_FMT_STRING);

	private final Button btnNew = UIUtils.getButton(null, get("txt.button.add"), this, "edit_add",
			GritTheme.BUTTON_LINK);

	private Button btnCopy = UIUtils.getButton(null, get("txt.button.copy"), this, "editcopy", GritTheme.BUTTON_LINK);

	private final Button btnEdit = UIUtils.getButton(null, get("txt.button.edit"), this, "color_line",
			GritTheme.BUTTON_LINK);

	private final Button btnDelete = UIUtils.getButton(null, get("txt.button.remove"), this, "edit_remove",
			GritTheme.BUTTON_LINK);

	private final Button btnDnlCsv = UIUtils.getButton(null, get("txt.button.csv"), null, "down",
			GritTheme.BUTTON_LINK);

	private final Button btnRefresh = UIUtils.getButton(null, get("txt.button.refresh"), this, "reload",
			GritTheme.BUTTON_LINK);

	private final Button btnNotifyAdd = UIUtils.getButton(null, get("txt.button.checktask.notifyAdd"), this,
			"mail_forward", GritTheme.BUTTON_LINK);

	/** Button zum Aktivieren oder Deaktivieren von Pruefauftraege */
	private final Button btnAktivieren = UIUtils.getButton(null, get("txt.button.checktask.deaktivieren"), this,
			"outtf", GritTheme.BUTTON_LINK);

	private VerticalLayout mainLay;

	private Label notification = new Label();

	private Label refreshed = new Label();

	private FilteredView<CheckTask> mainTable = new FilteredView<CheckTask>(CheckTask.class, "checktask.fields") {

		private static final long serialVersionUID = 4711381064320580238L;

		@Override
		protected void generatedColumns() {
			this.addColumn(CheckTask::checkDayToString) //
					.setId("checkDay");

			this.addColumn(CheckTask::getDateStart) //
					.setRenderer(new LocalDateRenderer(DATE_FMT_SHORT_STRING)) //
					.setId("_dateStart");
			this.addColumn(CheckTask::getDateEnd) //
					.setRenderer(new LocalDateRenderer(DATE_FMT_SHORT_STRING)) //
					.setId("_dateEnd");
			this.addColumn(ct -> {
				String sensortyp = ct.getSensor().getType();
				String res = sensorTypes.get(sensortyp);
				return (res != null) ? res : sensortyp;
			}) //
					.setId("_type");
		}
	};

	private Map<Long, String> notifyTypes = new HashMap<>();

	private Map<String, String> sensorTypes = new HashMap<>();

	private List<Actions> activate_typ = new ArrayList<>();

	private int daemonId;

	public CheckTaskPage() {
		super();
		buildMainLayout();
		setCompositionRoot(mainLay);
	}

	private void buildMainLayout() {
		mainLay = UIUtils.getDefaultRoot(null, btnNotifyAdd, btnCopy, btnNew, btnEdit, btnDelete, btnDnlCsv, btnRefresh,
				btnAktivieren);

		mainTable.addHeaderFilterRow(Messages.getSplittedArray("checktask.fields_names", "", ","));

		mainTable.setSelectionMode(SelectionMode.MULTI);

		mainTable.addSelectionListener(lsnr -> {
			changeButtons(lsnr.getAllSelectedItems().size());
			CheckTask elem = lsnr.getFirstSelectedItem().orElse(null);

			if (elem != null) {
				notification.setVisible(true);
				if (elem.getNotifications() != null && elem.getNotifications().size() > 0) {
					notification
							.setValue(get("checktask.notification.editor.notesAvail", elem.getNotifications().size()));
				} else {
					notification.setValue(get("checktask.notification.editor.notesNotAvail"));
				}
			} else {
				notification.setVisible(false);
			}
		});

		btnEdit.setEnabled(false);
		btnDelete.setEnabled(false);
		btnCopy.setEnabled(false);
		btnNotifyAdd.setEnabled(false);
		btnAktivieren.setEnabled(false);

		mainLay.addComponent(mainTable);
		mainLay.setExpandRatio(mainTable, 1.0f);

		mainLay.addComponent(notification);
		notification.setVisible(false);

		mainLay.addComponent(refreshed);

		mainTable.enableCSVExport(btnDnlCsv);

		UIUtils.setFullWidth(mainTable);
	}

	public void changeButtons(int size) {
		btnEdit.setEnabled(size == 1);
		btnDelete.setEnabled(size > 0);
		btnCopy.setEnabled(size == 1);
		btnNotifyAdd.setEnabled(size == 1);
		btnAktivieren.setEnabled(size > 0);
	}

	public void persistDelete(Object obj) {
		if (obj instanceof Collection<?>) {
			Ebean.deleteAll((Collection<?>) obj);
		} else {
			Ebean.delete((CheckTask) obj);
		}
		refresh();
	}

	public void persistSave(Object obj) throws OptimisticLockException {

		if (obj instanceof Collection<?>) {
			Ebean.saveAll((Collection<?>) obj);
		} else {
			Ebean.save((CheckTask) obj);
		}
		refresh();
	}

	@Override
	public void refresh() {
		daemonId = UIUtils.getDaemonId();

		notifyTypes.clear();
		for (MessageType mt : Ebean.find(MessageType.class).findList()) {
			notifyTypes.put(mt.getId(), mt.getName());
		}

		sensorTypes.clear();
		for (SensorTyp st : Ebean.find(SensorTyp.class).findList()) {
			sensorTypes.put(st.getTyp(), st.getName());
		}

		/** holt die verschiedenen Aktionen aus der Datenbank */
		activate_typ = Ebean.find(Actions.class).findList();

		List<CheckTask> lst = Ebean.find(CheckTask.class) // .fetch( "sensor" )
				.where().eq("sensor.daemonId", daemonId)//
				.findList();
		mainTable.update(lst);

		refreshed.setValue(get("txt.refreshed.at", DATE_FMT.format(new Date())));
	}

	@Override
	public void buttonClick(ClickEvent event) {
		try {
			if (event.getSource() == btnRefresh) {
				refresh();
			} else if (event.getSource() == btnEdit || event.getSource() == btnCopy) {
				CheckTask ct;
				ct = mainTable.getSelectionModel().getFirstSelectedItem().orElse(null);
				if (event.getSource() == btnEdit) {
					showEditor(ct);
				} else {
					CheckTask copyCt = new CheckTask();
					if (ct != null) {
						copyCt.setName(ct.getName() + " - copy");
						copyCt.setDateStart(ct.getDateStart());
						copyCt.setDateEnd(ct.getDateEnd());
						copyCt.setTimeStart(ct.getTimeStart());
						copyCt.setTimeEnd(ct.getTimeEnd());
						copyCt.setPause(ct.getPause());
						copyCt.setTimeout(ct.getTimeout());
						copyCt.setCheckday0Sun(ct.isCheckday0Sun());
						copyCt.setCheckday1Mon(ct.isCheckday1Mon());
						copyCt.setCheckday2Tue(ct.isCheckday2Tue());
						copyCt.setCheckday3Wed(ct.isCheckday3Wed());
						copyCt.setCheckday4Thu(ct.isCheckday4Thu());
						copyCt.setCheckday5Fri(ct.isCheckday5Fri());
						copyCt.setCheckday6Sat(ct.isCheckday6Sat());

						Sensor oldS = ct.getSensor();
						Sensor newS = new Sensor();
						newS.setDaemonId(oldS.getDaemonId());
						newS.setChecktask(oldS.getChecktask());
						newS.setName(oldS.getName());
						newS.setType(oldS.getType());

						SensorConfig oldSC = oldS.getConfig();
						SensorConfig newSC = new SensorConfig();
						newSC.setAppUser(oldSC.getAppUser());
						newSC.setAppPass(oldSC.getAppPass());
						newSC.setBbox(oldSC.getBbox());
						newSC.setCenterPos(oldSC.getCenterPos());
						newSC.setDbpAction(oldSC.getDbpAction());
						newSC.setDbpDatabase(oldSC.getDbpDatabase());
						newSC.setFeatureCap(oldSC.getFeatureCap());
						newSC.setFeatureGet(oldSC.getFeatureGet());
						newSC.setFmtName(oldSC.getFmtName());
						newSC.setFormatImgCheck(oldSC.getFormatImgCheck());
						newSC.setHttpPass(oldSC.getHttpPass());
						newSC.setHttpUser(oldSC.getHttpUser());
						newSC.setImageDir(oldSC.getImageDir());
						newSC.setImageSize(oldSC.getImageSize());
						newSC.setImsAction(oldSC.getImsAction());
						newSC.setImsHost(oldSC.getImsHost());
						newSC.setImsPipe(oldSC.getImsPipe());
						newSC.setLayerAvail(oldSC.getLayerAvail());
						newSC.setLayerImgCheck(oldSC.getLayerImgCheck());
						newSC.setMapMxd(oldSC.getMapMxd());
						newSC.setMapNameId(oldSC.getMapNameId());
						newSC.setProxy(oldSC.getProxy());
						newSC.setRegexpNot(oldSC.getRegexpNot());
						newSC.setRegexpReq(oldSC.getRegexpReq());
						newSC.setResDpi(oldSC.getResDpi());
						newSC.setRotation(oldSC.getRotation());
						newSC.setScale(oldSC.getScale());
						newSC.setSqlCmd(oldSC.getSqlCmd());
						newSC.setSrs(oldSC.getSrs());
						newSC.setStringProp(oldSC.getStringProp());
						newSC.setStylesImgCheck(oldSC.getStylesImgCheck());
						newSC.setUrl(oldSC.getUrl());
						newSC.setValidCode(oldSC.getValidCode());
						newSC.setVersionString(oldSC.getVersionString());

						newS.setConfig(newSC);
						copyCt.setSensor(newS);

						showEditor(copyCt);
					}
				}
			} else if (event.getSource() == btnNew) {
				CheckTask ct;
				ct = new CheckTask();
				chooseType(ct);
			} else if (event.getSource() == btnDelete) {
				// CheckTask selected =
				// mainTable.getSelectionModel().getFirstSelectedItem().orElse( null );
				// if ( selected != null ) {
				// ConfirmDialog.show( UI.getCurrent(), get( "txt.confirm.delete" ),
				// selected.getId() + " " + selected.getName(), get( "txt.button.yes" ),
				// get( "txt.button.no" ), dialog -> {
				// if ( dialog.isConfirmed() ) {
				// persistDelete( selected );
				// mainTable.deselectAll();
				// }
				// } );
				// }
				Set<CheckTask> selected = mainTable.getSelectionModel().getSelectedItems();
				String name = "";
				for (CheckTask c : selected) {
					name = name + c.getId() + " " + c.getName() + "\n";
				}

				ConfirmDialog.show(UI.getCurrent(), get("txt.confirm.delete"),
						selected.stream().map(c -> c.getId() + " " + c.getName()).collect(Collectors.joining("\n")),
						get("txt.button.yes"), get("txt.button.no"), dialog -> {
							if (dialog.isConfirmed()) {
								persistDelete(selected);
								mainTable.deselectAll();
							}
						});

			} else if (event.getSource() == btnNotifyAdd) {
				CheckTask ct;
				ct = mainTable.getSelectionModel().getFirstSelectedItem().orElse(null);

				// List<Person> persons = Ebean.find( Person.class ).where().ilike( "name")
				addNotificationEditor(ct);

			} else if (event.getSource() == btnAktivieren) {
				/**
				 * Entnimmt die aktuelle ausgewaehlte Pruefauftrage aus der Tabelle, um sie zu
				 * aktivieren/deaktivieren
				 */
				// CheckTask selected =
				// mainTable.getSelectionModel().getFirstSelectedItem().orElse(null);
				Set<CheckTask> selected = mainTable.getSelectionModel().getSelectedItems();

				/** ComboBox zur Anzeige von Aktionen auf der Benutzeroberflaeche */
				ComboBox<Actions> Aktion = new ComboBox<>(get("checktask.editor.action"));
				Aktion.setItemCaptionGenerator(item -> item.getName());
				/** Setzt die Standardauswahl auf das erste Element */
				Aktion.setValue(activate_typ.get(0));
				Aktion.setItems(activate_typ);
				Aktion.setEmptySelectionAllowed(false);
				UIUtils.setFullWidth(Aktion);
				Window wnd = UIUtils.buildInputWindow(get("checktask.editor.pruefauftrag"))
						.withOkButtonCaption(get("txt.button.select")).withResultOk((src, bnd) -> {
							try {
								/** Entnimmt die aktuelle Aktion */
								Actions son = Aktion.getSelectedItem().orElse(null);
								/**
								 * Speichert die ausgewaehlte Pruefauftrage, die aktiviert oder deaktiviert
								 * wurden
								 */
								persistSave(activateTyp(selected, son));
								mainTable.deselectAll();
							} catch (Exception ex) {
								LOG.error("Fehler beim speichern: {}", ex.getLocalizedMessage());
								LOG.trace("", ex);
								UIUtils.showNotification(get("txt.error.general-form"), //$NON-NLS-1$
										Type.WARNING_MESSAGE);
							}
						}).withCancelButtonCaption(get("txt.button.close")) //
						.withLayout(new FormLayout()) //
						.withComponents(Aktion) //
						.build();

				wnd.center();
				wnd.setWidth(400.0f, Unit.PIXELS);
				wnd.setHeightUndefined();
				UIUtils.setFullWidth(wnd.getContent());
				UI.getCurrent().addWindow(wnd);
			}
		} catch (Exception ex) {
			LOG.warn("Unexpected Ex: {}", ex.getMessage());
			LOG.warn("Exception", ex);
			UIUtils.showNotification("Unerwarteter Fehler", ex.getMessage(), Type.WARNING_MESSAGE);
		}
	}

	private void chooseType(CheckTask ct) {
		List<String> data;
		data = sensorTypes.keySet().stream() //
				.sorted((s1, s2) -> s1.compareTo(s2)) //
				.collect(Collectors.toList());

		ComboBox<String> type = new ComboBox<>(get("checktask.fields.sensor.type"), data);
		type.setItemCaptionGenerator(item -> sensorTypes.get(item));
		type.setEmptySelectionAllowed(false);

		UIUtils.setFullWidth(type);

		Window wnd = UIUtils.buildInputWindow(get("txt.caption.checkTask-Editor")) //
				.withOkButtonCaption(get("txt.button.select")) //
				.withResultOk((src, bnd) -> {
					try {
						ct.setCheckday1Mon(true);
						ct.setCheckday2Tue(true);
						ct.setCheckday3Wed(true);
						ct.setCheckday4Thu(true);
						ct.setCheckday5Fri(true);
						ct.setCheckday6Sat(true);
						ct.setCheckday0Sun(true);

						try {
							String tmp = UIUtils.getProp("checktask." + type + ".defaultTimeout",
									UIUtils.getProp("checktask.defaultTimeout", "30"));
							if (tmp != null)
								ct.setTimeout(new Integer(Integer.parseInt(tmp)));
						} catch (Exception ex) {
							LOG.error("Failed Loading defaultTimeout / timeout for {}", type);
						}

						try {
							String tmp = UIUtils.getProp("checktask." + type + ".defaultPause",
									UIUtils.getProp("checktask.defaultPause", "1800")); // 1800s = 30m
							if (tmp != null)
								ct.setPause(new Integer(Integer.parseInt(tmp)));
						} catch (Exception ex) {
							LOG.error("Failed Loading defaultPause / pause for {}", type);
						}

						Sensor s = new Sensor();
						s.setDaemonId(UIUtils.getDaemonId());
						s.setName("* AUTOGENERATED SENSOR *");
						s.setConfig(new SensorConfig());
						s.setType(type.getSelectedItem().orElse(null));
						ct.setSensor(s);
						showEditor(ct);
					} catch (Exception ex) {
						LOG.error("Fehler beim speichern: {}", ex.getLocalizedMessage());
						LOG.trace("", ex);
						UIUtils.showNotification(get("txt.error.general-form"), //$NON-NLS-1$
								Type.WARNING_MESSAGE);
					}
				}) //
				.withCancelButtonCaption(get("txt.button.close")) //
				.withLayout(new FormLayout()) //
				.withComponents(type) //
				.build();

		wnd.center();
		wnd.setWidth(400.0f, Unit.PIXELS);
		wnd.setHeightUndefined();
		UIUtils.setFullWidth(wnd.getContent());
		UI.getCurrent().addWindow(wnd);
	}

	private void showEditor(CheckTask ct) {
		Binder<CheckTask> binder = new Binder<>(CheckTask.class);

		Panel p = new Panel(get("checktask.editor.form_basic"));

		TextField name = new TextField(get("checktask.fields.name"));
		binder.forField(name) //
				.asRequired(get("checktask.fields.name")) //
				.bind(CheckTask::getName, CheckTask::setName);

		final DateField startDate = new DateField(get("checktask.fields.dateStart"));
		startDate.setParseErrorMessage(get("txt.error.val.date"));
		binder.forField(startDate) //
				.bind(CheckTask::getDateStart, CheckTask::setDateStart);

		final DateField endDate = new DateField(get("checktask.fields.dateEnd"));
		endDate.setParseErrorMessage(get("txt.error.val.date"));
		binder.forField(endDate) //
				.bind(CheckTask::getDateEnd, CheckTask::setDateEnd);

		TextField startTime = new TextField(get("checktask.fields.timeStart"));
		binder.forField(startTime) //
				.bind(CheckTask::getTimeStart, CheckTask::setTimeStart);

		TextField endTime = new TextField(get("checktask.fields.timeEnd"));
		binder.forField(endTime) //
				.bind(CheckTask::getTimeEnd, CheckTask::setTimeEnd);

		TextField pause = new TextField(get("checktask.editor_field.pause"));
		binder.forField(pause) //
				.asRequired(get("checktask.editor_field.pause")) //
				.withConverter(new StringToIntegerConverter(get("txt.error.val.integer"))) //
				.bind(CheckTask::getPause, CheckTask::setPause);

		TextField timeout = new TextField(get("checktask.editor_field.timeout"));
		binder.forField(timeout) //
				.asRequired(get("checktask.editor_field.timeout")) //
				.withConverter(new StringToIntegerConverter(get("txt.error.val.integer"))) //
				.bind(CheckTask::getTimeout, CheckTask::setTimeout);

		List<String> data = Arrays.asList(get("checktask.editor_field.checkday0Sun"),
				get("checktask.editor_field.checkday1Mon"), get("checktask.editor_field.checkday2Tue"),
				get("checktask.editor_field.checkday3Wed"), get("checktask.editor_field.checkday4Thu"),
				get("checktask.editor_field.checkday5Fri"), get("checktask.editor_field.checkday6Sat"));
		CheckBoxGroup<String> days = new CheckBoxGroup<>(get("checktask.editor_field.checkdays"), data);
		days.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		binder.forField(days) //
				.bind(checkTask -> {
					// Getter
					Set<String> res = new HashSet<>();
					res.add((checkTask.isCheckday0Sun() ? get("checktask.editor_field.checkday0Sun") : ""));
					res.add((checkTask.isCheckday1Mon() ? get("checktask.editor_field.checkday1Mon") : ""));
					res.add((checkTask.isCheckday2Tue() ? get("checktask.editor_field.checkday2Tue") : ""));
					res.add((checkTask.isCheckday3Wed() ? get("checktask.editor_field.checkday3Wed") : ""));
					res.add((checkTask.isCheckday4Thu() ? get("checktask.editor_field.checkday4Thu") : ""));
					res.add((checkTask.isCheckday5Fri() ? get("checktask.editor_field.checkday5Fri") : ""));
					res.add((checkTask.isCheckday6Sat() ? get("checktask.editor_field.checkday6Sat") : ""));
					return res;
				}, (checkTask, value) -> {
					// Setter
					checkTask.setCheckday0Sun(value.contains(get("checktask.editor_field.checkday0Sun")));
					checkTask.setCheckday1Mon(value.contains(get("checktask.editor_field.checkday1Mon")));
					checkTask.setCheckday2Tue(value.contains(get("checktask.editor_field.checkday2Tue")));
					checkTask.setCheckday3Wed(value.contains(get("checktask.editor_field.checkday3Wed")));
					checkTask.setCheckday4Thu(value.contains(get("checktask.editor_field.checkday4Thu")));
					checkTask.setCheckday5Fri(value.contains(get("checktask.editor_field.checkday5Fri")));
					checkTask.setCheckday6Sat(value.contains(get("checktask.editor_field.checkday6Sat")));
				});

		HorizontalLayout times = new HorizontalLayout();
		times.addComponents(startDate, endDate, startTime, endTime);

		HorizontalLayout proof = new HorizontalLayout();
		proof.addComponents(pause, timeout, days);

		UIUtils.setFullWidth(name);

		p.setContent(new VerticalLayout(name, times, proof));

		String sensortyp = ct.getSensor().getType();

		Panel param = createTestingParams(sensortyp, binder);

		binder.readBean(ct);

		Window wnd = UIUtils.buildInputWindow(get("checktask.editor.caption", sensorTypes.get(sensortyp)), binder) //
				.withOkButtonCaption(get("txt.button.save-and-close")) //
				.withResultOk((src, bnd) -> {
					try {
						if (bnd.writeBeanIfValid(ct)) {
							persistSave(ct);
							src.close();
						} else {
							UIUtils.showNotification(get("txt.error.val"), Type.WARNING_MESSAGE);
						}
					} catch (Exception ex) {
						LOG.error("Fehler beim speichern: {}", ex.getLocalizedMessage());
						LOG.trace("", ex);
						UIUtils.showNotification(get("txt.error.general-form"), //$NON-NLS-1$
								Type.WARNING_MESSAGE);
					}
				}) //
				.withCancelButtonCaption(get("txt.button.close")) //
				.withLayout(new FormLayout()) //
				.withComponents(p, param) //
				.withEmptyNotAllowed() //
				.build();

		wnd.center();
		wnd.setWidth(60.0f, Unit.PERCENTAGE);
		wnd.setHeightUndefined();
		UIUtils.setFullWidth(wnd.getContent());
		UI.getCurrent().addWindow(wnd);
	}

	@SuppressWarnings("null")
	private Panel createTestingParams(String sensortyp, Binder<CheckTask> binder) {

		// TODO text-align-last: right;
		Panel p = new Panel(get("checktask.editor.form_param"));
		FormLayout param = new FormLayout();
		param.setMargin(true);

		TextField url;
		Label urlTxt;
		ComboBox<String> version;
		Label orientationHint;
		TextField proxy;
		Label proxyTxt;
		TextField httpUser;
		DualPasswordField httpPass;
		TextField LayerAvail;
		Label LayerAvailTxt;
		ComboBox<String> rotation;
		TextField bbox;
		Label bboxTxt;
		TextField appUser;
		DualPasswordField appPass;
		TextField sqlCmd;
		Label sqlTxt;
		TextField collectionidAvailable = new TextField(get("checktask.editor.collectionAvailable"));
		;
		TextField collectionidContent = new TextField(get("checktask.editor.collectionsContent"));
		Label collectionidAvailableText;
		Label collectionidContentText;
		Label featureidText;
		TextField featureid = new TextField(get("checktask.editor.featureGet"));
		Label nameStatus = new Label();

		switch (sensortyp) {
		case "WFSBASIC":
			url = new TextField(get("checktask.editor.url"));
			binder.forField(url).asRequired(get("checktask.editor.url")).bind(ct -> {
				return ct.getSensor().getConfig().getUrl();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setUrl(value);
			});

			version = new ComboBox<>(get("checktask.editor.versionString_WFSBASIC"),
					Arrays.asList(UIUtils.getProp("CheckTaskWFS.versions", "1.1.0,2.0").split(",")));
			binder.forField(version).asRequired(get("checktask.editor.versionString_WFSBASIC")).bind(ct -> {
				return ct.getSensor().getConfig().getVersionString();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setVersionString(value);
			});

			TextField featureCap = new TextField(get("checktask.editor.featureCap"));
			binder.forField(featureCap).bind(ct -> {
				return ct.getSensor().getConfig().getFeatureCap();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setFeatureCap(value);
			});

			Label featureCapTxt = new Label(get("checktask.editor.featureCap_tip"), ContentMode.HTML);
			featureCapTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField featureGet = new TextField(get("checktask.editor.featureGet"));
			binder.forField(featureGet).bind(ct -> {
				return ct.getSensor().getConfig().getFeatureGet();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setFeatureGet(value);
			});

			Label featureGetTxt = new Label(get("checktask.editor.featureGet_tip"), ContentMode.HTML);
			featureGetTxt.addStyleName(ValoTheme.LABEL_SMALL);

			proxy = new TextField(get("checktask.editor.proxy"));
			binder.forField(proxy).bind(ct -> {
				return ct.getSensor().getConfig().getProxy();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setProxy(value);
			});
			proxyTxt = new Label(get("checktask.editor.proxy_tip"), ContentMode.HTML);
			proxyTxt.addStyleName(ValoTheme.LABEL_SMALL);

			httpUser = new TextField(get("checktask.editor.httpUser"));
			binder.forField(httpUser).bind(ct -> {
				return ct.getSensor().getConfig().getHttpUser();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setHttpUser(value);
			});

			httpPass = new DualPasswordField(get("checktask.editor.httpPass"));
			binder.forField(httpPass).withNullRepresentation("")
					.withValidator(
							new DualPasswordField.PasswordValidator(httpPass, get("txt.error.val.passwordcheck"))) //
					.bind(ct -> {
						return ct.getSensor().getConfig().getHttpPass();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setHttpPass(value);
					});

			UIUtils.setFullWidth(url, featureCap, featureCapTxt, featureGet, featureGetTxt, proxy, proxyTxt, httpUser,
					httpPass);

			param.addComponents(url, version, featureCap, featureCapTxt, featureGet, featureGetTxt, proxy, proxyTxt,
					httpUser, httpPass);
			break;
		case "WMSBASIC":
			url = new TextField(get("checktask.editor.url"));
			binder.forField(url).asRequired(get("checktask.editor.url")).bind(ct -> {
				return ct.getSensor().getConfig().getUrl();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setUrl(value);
			});

			orientationHint = new Label(get("checktask.editor.orientation"), ContentMode.HTML);
			orientationHint.addStyleName(ValoTheme.LABEL_SMALL);
			version = new ComboBox<>(get("checktask.editor.versionString_WMSBASIC"),
					Arrays.asList(UIUtils.getProp("CheckTaskWMS.versions", "1.0.1,1.1.0,1.1.1,1.3.0").split(",")));

			List<String> rotationValues = Arrays
					.asList(UIUtils.getProp("CheckTaskWMS.rotation", "X-Y-Reihenfolge;Y-X-Reihenfolge").split(";"));
			rotation = new ComboBox<>(get("checktask.editor.rotation_WMSBASIC"), rotationValues);
			rotation.setEmptySelectionAllowed(false);
			rotation.setSelectedItem(rotationValues.get(0));
			rotation.setWidth(150.0f, Unit.PIXELS);
			binder.forField(rotation).bind(ct -> {
				Integer rot = ct.getSensor().getConfig().getRotation();
				return rotationValues.get(rot != null ? rot : 0);
			}, (ct, value) -> {
				ct.getSensor().getConfig().setRotation(rotationValues.indexOf(value));
			});

			version.addSelectionListener(newValue -> {
				if ("1.3.0".equals(newValue.getValue())) {
					orientationHint.setVisible(true);
					rotation.setVisible(true);
				} else {
					orientationHint.setVisible(false);
					rotation.setVisible(false);
				}
			});

			binder.forField(version).asRequired(get("checktask.editor.versionString_WFSBASIC")).bind(ct -> {
				return ct.getSensor().getConfig().getVersionString();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setVersionString(value);
			});

			LayerAvail = new TextField(get("checktask.editor.layerAvail"));
			binder.forField(LayerAvail).bind(ct -> {
				return ct.getSensor().getConfig().getLayerAvail();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setLayerAvail(value);
			});

			LayerAvailTxt = new Label(get("checktask.editor.layerAvail_tip"), ContentMode.HTML);
			LayerAvailTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField LayerImgCheck = new TextField(get("checktask.editor.layerImgCheck"));
			binder.forField(LayerImgCheck).bind(ct -> {
				return ct.getSensor().getConfig().getLayerImgCheck();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setLayerImgCheck(value);
			});

			Label LayerImgCheckTxt = new Label(get("checktask.editor.layerImgCheck_tip"), ContentMode.HTML);
			LayerImgCheckTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField StylesImgCheck = new TextField(get("checktask.editor.stylesImgCheck"));
			binder.forField(StylesImgCheck).bind(ct -> {
				return ct.getSensor().getConfig().getStylesImgCheck();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setStylesImgCheck(value);
			});

			Label StylesImgCheckTxt = new Label(get("checktask.editor.stylesImgCheck_tip"), ContentMode.HTML);
			StylesImgCheckTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField srs = new TextField(get("checktask.editor.srs"));
			binder.forField(srs).bind(ct -> {
				return ct.getSensor().getConfig().getSrs();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setSrs(value);
			});

			Label srsTxt = new Label(get("checktask.editor.srs_tip"), ContentMode.HTML);
			srsTxt.addStyleName(ValoTheme.LABEL_SMALL);

			bbox = new TextField(get("checktask.editor.bbox"));
			binder.forField(bbox).bind(ct -> {
				return ct.getSensor().getConfig().getBbox();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setBbox(value);
			});

			bboxTxt = new Label(get("checktask.editor.bbox_tip"), ContentMode.HTML);
			bboxTxt.addStyleName(ValoTheme.LABEL_SMALL);

			proxy = new TextField(get("checktask.editor.proxy"));
			binder.forField(proxy).bind(ct -> {
				return ct.getSensor().getConfig().getProxy();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setProxy(value);
			});

			proxyTxt = new Label(get("checktask.editor.proxy_tip"), ContentMode.HTML);
			proxyTxt.addStyleName(ValoTheme.LABEL_SMALL);

			httpUser = new TextField(get("checktask.editor.httpUser"));
			binder.forField(httpUser).bind(ct -> {
				return ct.getSensor().getConfig().getHttpUser();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setHttpUser(value);
			});

			httpPass = new DualPasswordField(get("checktask.editor.httpPass"));
			binder.forField(httpPass).withNullRepresentation("")
					.withValidator(
							new DualPasswordField.PasswordValidator(httpPass, get("txt.error.val.passwordcheck"))) //
					.bind(ct -> {
						return ct.getSensor().getConfig().getHttpPass();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setHttpPass(value);
					});

			UIUtils.setFullWidth(url, LayerAvail, LayerAvailTxt, LayerImgCheck, LayerImgCheckTxt, StylesImgCheck,
					StylesImgCheckTxt, srs, srsTxt, bbox, orientationHint, bboxTxt, proxy, proxyTxt, httpUser,
					httpPass);

			param.addComponents(url, version, LayerAvail, LayerAvailTxt, LayerImgCheck, LayerImgCheckTxt,
					StylesImgCheck, StylesImgCheckTxt, srs, srsTxt, rotation, bbox, orientationHint, bboxTxt, proxy,
					proxyTxt, httpUser, httpPass);
			break;
		case "HTTPCHK":
			url = new TextField(get("checktask.editor.url"));
			binder.forField(url).asRequired(get("checktask.editor.url")).bind(ct -> {
				return ct.getSensor().getConfig().getUrl();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setUrl(value);
			});

			proxy = new TextField(get("checktask.editor.proxy"));
			binder.forField(proxy).bind(ct -> {
				return ct.getSensor().getConfig().getProxy();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setProxy(value);
			});

			proxyTxt = new Label(get("checktask.editor.proxy_tip"), ContentMode.HTML);
			proxyTxt.addStyleName(ValoTheme.LABEL_SMALL);

			httpUser = new TextField(get("checktask.editor.httpUser"));
			binder.forField(httpUser).bind(ct -> {
				return ct.getSensor().getConfig().getHttpUser();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setHttpUser(value);
			});

			httpPass = new DualPasswordField(get("checktask.editor.httpPass"));
			binder.forField(httpPass) //
					.withNullRepresentation("")
					.withValidator(
							new DualPasswordField.PasswordValidator(httpPass, get("txt.error.val.passwordcheck")))
					.bind(ct -> {
						return ct.getSensor().getConfig().getHttpPass();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setHttpPass(value);
					});

			TextField regexpReq = new TextField(get("checktask.editor.regexpReq"));
			binder.forField(regexpReq).bind(ct -> {
				return ct.getSensor().getConfig().getRegexpReq();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setRegexpReq(value);
			});

			Label regexpReqTxt = new Label(get("checktask.editor.regexpReq_tip"), ContentMode.HTML);
			regexpReqTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField regexpNot = new TextField(get("checktask.editor.regexpNot"));
			binder.forField(regexpNot).bind(ct -> {
				return ct.getSensor().getConfig().getRegexpNot();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setRegexpNot(value);
			});

			Label regexpNotTxt = new Label(get("checktask.editor.regexpNot_tip"), ContentMode.HTML);
			regexpNotTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField validCode = new TextField(get("checktask.editor.validCode"));
			binder.forField(validCode).bind(ct -> {
				return ct.getSensor().getConfig().getValidCode();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setValidCode(value);
			});

			Label validCodeTxt = new Label(get("checktask.editor.validCode_tip"), ContentMode.HTML);
			validCodeTxt.addStyleName(ValoTheme.LABEL_SMALL);

			UIUtils.setFullWidth(url, proxy, proxyTxt, httpUser, httpPass, regexpReq, regexpReqTxt, regexpNot,
					regexpNotTxt, validCode, validCodeTxt);

			param.addComponents(url, proxy, proxyTxt, httpUser, httpPass, regexpReq, regexpReqTxt, regexpNot,
					regexpNotTxt, validCode, validCodeTxt);
			break;
		case "SQLORACLE":
		case "SQLPG":
			url = new TextField((sensortyp.equals("SQLORACLE") ? get("checktask.editor.url_SQLORACLE")
					: get("checktask.editor.url_SQLPG")));
			binder.forField(url).asRequired(sensortyp.equals("SQLORACLE") ? get("checktask.editor.url_SQLORACLE")
					: get("checktask.editor.url_SQLPG")).bind(ct -> {
						return ct.getSensor().getConfig().getUrl();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setUrl(value);
					});
			urlTxt = new Label((sensortyp.equals("SQLORACLE") ? get("checktask.editor.url_tip_SQLORACLE")
					: get("checktask.editor.url_tip_SQLPG")), ContentMode.HTML);
			urlTxt.addStyleName(ValoTheme.LABEL_SMALL);

			appUser = new TextField(get("checktask.editor.appUser"));
			binder.forField(appUser).asRequired(get("checktask.editor.appUser")).bind(ct -> {
				return ct.getSensor().getConfig().getAppUser();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setAppUser(value);
			});

			appPass = new DualPasswordField(get("checktask.editor.appPass"));
			binder.forField(appPass).withNullRepresentation("")
					.withValidator(new DualPasswordField.PasswordValidator(appPass, get("txt.error.val.passwordcheck"))) //
					.asRequired(get("checktask.editor.appPass")).bind(ct -> {
						return ct.getSensor().getConfig().getAppPass();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setAppPass(value);
					});

			sqlCmd = new TextField(get("checktask.editor.sqlCmd"));
			binder.forField(sqlCmd).bind(ct -> {
				return ct.getSensor().getConfig().getSqlCmd();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setSqlCmd(value);
			});

			sqlTxt = new Label(get("checktask.editor.sqlCmd_tip"), ContentMode.HTML);
			sqlTxt.addStyleName(ValoTheme.LABEL_SMALL);

			UIUtils.setFullWidth(url, urlTxt, appUser, appPass, sqlCmd, sqlTxt);

			param.addComponents(url, urlTxt, appUser, appPass, sqlCmd, sqlTxt);
			break;
		case "DBPBASIC":
			url = new TextField(get("checktask.editor.url"));
			binder.forField(url).asRequired(get("checktask.editor.url")).bind(ct -> {
				return ct.getSensor().getConfig().getUrl();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setUrl(value);
			});

			httpUser = new TextField(get("checktask.editor.httpUser"));
			binder.forField(httpUser).bind(ct -> {
				return ct.getSensor().getConfig().getHttpUser();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setHttpUser(value);
			});

			httpPass = new DualPasswordField(get("checktask.editor.httpPass"));
			binder.forField(httpPass).withNullRepresentation("")
					.withValidator(
							new DualPasswordField.PasswordValidator(httpPass, get("txt.error.val.passwordcheck")))
					.bind(ct -> {
						return ct.getSensor().getConfig().getHttpPass();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setHttpPass(value);
					});

			TextField dbpDatabase = new TextField(get("checktask.editor.dbpDatabase"));
			binder.forField(dbpDatabase).bind(ct -> {
				return ct.getSensor().getConfig().getDbpDatabase();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setDbpDatabase(value);
			});

			appUser = new TextField(get("checktask.editor.appUser"));
			binder.forField(appUser).bind(ct -> {
				return ct.getSensor().getConfig().getAppUser();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setAppUser(value);
			});

			appPass = new DualPasswordField(get("checktask.editor.appPass"));
			binder.forField(appPass).withNullRepresentation("")
					.withValidator(new DualPasswordField.PasswordValidator(appPass, get("txt.error.val.passwordcheck")))
					.bind(ct -> {
						return ct.getSensor().getConfig().getAppPass();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setAppPass(value);
					});

			ComboBox<String> dbpAction = new ComboBox<>(get("checktask.editor.dbpAction"),
					Arrays.asList(Messages.getSplittedArray("checktask.editor.dbpAction.opt", "", ",")));
			dbpAction.setItemCaptionGenerator(item -> get("checktask.editor.dbpAction.opt." + item));
			dbpAction.setEmptySelectionAllowed(false);
			binder.forField(dbpAction).asRequired(get("checktask.editor.dbpAction"))
					.withConverter(new StringToIntegerConverter("Fehlt noch!")).bind(ct -> {
						return ct.getSensor().getConfig().getDbpAction();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setDbpAction(value);
					});

			sqlCmd = new TextField(get("checktask.editor.sqlCmd"));
			binder.forField(sqlCmd).bind(ct -> {
				return ct.getSensor().getConfig().getSqlCmd();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setSqlCmd(value);
			});

			sqlTxt = new Label(get("checktask.editor.sqlCmd_tip"), ContentMode.HTML);
			sqlTxt.addStyleName(ValoTheme.LABEL_SMALL);

			UIUtils.setFullWidth(url, httpUser, httpPass, dbpDatabase, appUser, appPass, sqlCmd, sqlTxt);

			param.addComponents(url, httpUser, httpPass, dbpDatabase, appUser, appPass, dbpAction, sqlCmd, sqlTxt);
			break;
		case "ARCGIS":
			url = new TextField(get("checktask.editor.url_ARCGIS"));
			binder.forField(url).asRequired(get("checktask.editor.url_ARCGIS")).bind(ct -> {
				return ct.getSensor().getConfig().getUrl();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setUrl(value);
			});

			urlTxt = new Label(get("checktask.editor.url_tip_ARCGIS"), ContentMode.HTML);
			urlTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField mapMxd = new TextField(get("checktask.editor.mapMxd"));
			binder.forField(mapMxd).asRequired(get("checktask.editor.mapMxd")).bind(ct -> {
				return ct.getSensor().getConfig().getMapMxd();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setMapMxd(value);
			});

			LayerAvail = new TextField(get("checktask.editor.layerAvail"));
			binder.forField(LayerAvail).bind(ct -> {
				return ct.getSensor().getConfig().getLayerAvail();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setLayerAvail(value);
			});

			LayerAvailTxt = new Label(get("checktask.editor.layerAvail_tip"), ContentMode.HTML);
			LayerAvailTxt.addStyleName(ValoTheme.LABEL_SMALL);

			bbox = new TextField(get("checktask.editor.bbox"));
			binder.forField(bbox).bind(ct -> {
				return ct.getSensor().getConfig().getBbox();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setBbox(value);
			});

			bboxTxt = new Label(get("checktask.editor.bbox_tip"), ContentMode.HTML);
			bboxTxt.addStyleName(ValoTheme.LABEL_SMALL);

			TextField imgSize = new TextField(get("checktask.editor.imageSize"));
			binder.forField(imgSize).bind(ct -> {
				return ct.getSensor().getConfig().getImageSize();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setImageSize(value);
			});

			Label imgSizeTxt = new Label(get("checktask.editor.imageSize_tip"), ContentMode.HTML);
			imgSizeTxt.addStyleName(ValoTheme.LABEL_SMALL);

			UIUtils.setFullWidth(url, urlTxt, mapMxd, LayerAvail, LayerAvailTxt, bbox, bboxTxt, imgSize, imgSizeTxt);

			param.addComponents(url, urlTxt, mapMxd, LayerAvail, LayerAvailTxt, bbox, bboxTxt, imgSize, imgSizeTxt);
			break;

		case "OAFBASIC":

			/**
			 * nimmt die Basis-URL von der Schnittstelle und speichert sie in der Datenbank
			 * unter Verwendung des pojo
			 */
			url = new TextField(get("checktask.editor.url"));
			binder.forField(url).asRequired(get("checktask.editor.url")).bind(ct -> {
				return ct.getSensor().getConfig().getUrl();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setUrl(value);
			});
			urlTxt = new Label(get("checktask.editor.oafurl_tip"), ContentMode.HTML);
			urlTxt.addStyleName(ValoTheme.LABEL_SMALL);

			Label info = new Label();
			/**
			 * nimmt der Collection von der Schnittstelle und speichert sie in der Datenbank
			 * unter Verwendung des pojo
			 */
			// collectionid = new TextField( get( "checktask.editor.collections" )
			// );featureid,collectionidContent
			binder.forField(collectionidAvailable)
					.withValidator(ft -> validate(collectionidAvailable, collectionidContent, featureid) == true,
							"Eine Prüfung muss ausgefüllt werden")
					.withStatusLabel(info).bind(ct -> {
						return ct.getSensor().getConfig().getFeatureCap();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setFeatureCap(value);
					});

			collectionidAvailableText = new Label(get("checktask.editor.collectionidAvailable_tip"), ContentMode.HTML);
			collectionidAvailableText.addStyleName(ValoTheme.LABEL_SMALL);
			collectionidAvailable.setId("A");

			/**
			 * nimmt der Collection aus dem Gui von der Schnittstelle und speichert sie in
			 * der Datenbank unter Verwendung des pojo
			 */
			// collectionid1 = new TextField( get( "checktask.editor.collections1" ) );
			binder.forField(collectionidContent).bind(ct -> {
				return ct.getSensor().getConfig().getLayerAvail();
			}, (ct, value) -> {
				ct.getSensor().getConfig().setLayerAvail(value);
			});

			collectionidContentText = new Label(get("checktask.editor.collectionidContent_tip"), ContentMode.HTML);
			collectionidContentText.addStyleName(ValoTheme.LABEL_SMALL);

			/**
			 * nimmt das Feature aus der Gui und speichert es in der Datenbank unter
			 * Verwendung des pojo
			 */
			// featureid = new TextField( get( "checktask.editor.featureGet" ) ); validate

			binder.forField(featureid)
					.withValidator(ft -> validate(ft) == true,
							"keine Collection, featureId oder '}' vorhanden: Syntax im Editor beachten")
					.withStatusLabel(info).bind(ct -> {
						return ct.getSensor().getConfig().getFeatureGet();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setFeatureGet(value);
					});

			featureidText = new Label(get("checktask.editor.featureid_tip"), ContentMode.HTML);
			featureidText.addStyleName(ValoTheme.LABEL_SMALL);

			/**
			 * nimmt den Proxyserver aus dem Gui und speichert ihn in der Datenbank unter
			 * Verwendung des pojo
			 */
			proxy = new TextField(get("checktask.editor.proxy"));
			binder.forField(proxy) //
					.bind(ct -> {
						return ct.getSensor().getConfig().getProxy();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setProxy(value);
					});
			proxyTxt = new Label(get("checktask.editor.proxy_tip"), ContentMode.HTML);
			proxyTxt.addStyleName(ValoTheme.LABEL_SMALL);

			/**
			 * nimmt den HttpUser aus dem Gui und speichert ihn in der Datenbank unter
			 * Verwendung des pojo
			 */
			httpUser = new TextField(get("checktask.editor.httpUser"));
			binder.forField(httpUser) //
					.bind(ct -> {
						return ct.getSensor().getConfig().getHttpUser();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setHttpUser(value);
					});

			/**
			 * nimmt den HttpUser password aus dem Gui und speichert sie in der Datenbank
			 * unter Verwendung des pojo
			 */
			httpPass = new DualPasswordField(get("checktask.editor.httpPass"));
			binder.forField(httpPass) //
					.withNullRepresentation("") //
					.withValidator(
							new DualPasswordField.PasswordValidator(httpPass, get("txt.error.val.passwordcheck"))) //
					.bind(ct -> {
						return ct.getSensor().getConfig().getHttpPass();
					}, (ct, value) -> {
						ct.getSensor().getConfig().setHttpPass(value);
					});

			UIUtils.setFullWidth(url, urlTxt, collectionidAvailable, collectionidAvailableText, collectionidContent,
					collectionidContentText, featureid, featureidText, nameStatus, proxy, proxyTxt, httpUser, httpPass);

			param.addComponents(url, urlTxt, collectionidAvailable, collectionidAvailableText, collectionidContent,
					collectionidContentText, featureid, featureidText, nameStatus, proxy, proxyTxt, httpUser, httpPass);

			break;

		}

		/**
		 * ein Textfeld in Abhaengigkeit von der auszufuehrenden Kontrolle deaktivieren!
		 */
		collectionidAvailable.addValueChangeListener(s -> {
			if (s.getValue() != null && s.getValue().isEmpty()) {
				collectionidContent.setEnabled(true);
				featureid.setEnabled(true);
			} else {
				collectionidAvailable.setDescription("");
				collectionidContent.setEnabled(false);
				collectionidContent.setDescription(get("txt.error.unique"));
				featureid.setEnabled(false);
				featureid.setDescription(get("txt.error.unique"));
			}
		});

		collectionidContent.addValueChangeListener(s -> {
			if (s.getValue() != null && s.getValue().isEmpty()) {
				collectionidAvailable.setEnabled(true);
				featureid.setEnabled(true);
			} else {
				collectionidContent.setDescription("");
				collectionidAvailable.setEnabled(false);
				collectionidAvailable.setDescription(get("txt.error.unique"));
				featureid.setEnabled(false);
				featureid.setDescription(get("txt.error.unique"));
			}
		});

		featureid.addValueChangeListener(s -> {
			if (s.getValue() != null && s.getValue().isEmpty()) {
				collectionidAvailable.setEnabled(true);
				collectionidContent.setEnabled(true);
			} else {
				featureid.setDescription("");
				collectionidAvailable.setEnabled(false);
				collectionidAvailable.setDescription(get("txt.error.unique"));
				collectionidContent.setEnabled(false);
				collectionidContent.setDescription(get("txt.error.unique"));
			}
		});

		p.setContent(param);
		return p;

	}

	private void addNotificationEditor(CheckTask ct) {

		TwinColSelect<Person> mail = new TwinColSelect<>(get("checktask.notification.editor.mailSelect"));
		List<Person> pers = Ebean.find(Person.class).where().isNotNull("mail").findList();
		mail.setItemCaptionGenerator(p -> {
			return p.getName() + ", " + p.getMail();
		});
		mail.setItems(pers);

		TwinColSelect<Person> snmp = new TwinColSelect<>(get("checktask.notification.editor.snmpSelect"));
		pers = Ebean.find(Person.class).where().isNotNull("snmp").findList();
		snmp.setItemCaptionGenerator(p -> {
			return p.getName() + ", " + p.getSnmp();
		});
		snmp.setItems(pers);
		snmp.setHeight(150.0f, Unit.PIXELS);

		TwinColSelect<Person> snmp2 = new TwinColSelect<>(get("checktask.notification.editor.snmp2Select"));
		snmp2.setItemCaptionGenerator(p -> {
			return p.getName() + ", " + p.getSnmp();
		});
		snmp2.setItems(pers);
		snmp2.setHeight(150.0f, Unit.PIXELS);

		if (ct.getNotifications() != null) {
			Person[] selectedPers = ct.getNotifications().stream().filter(x -> {
				return x.getType() == 1;
			}).map(Notification::getPerson).toArray(Person[]::new);

			mail.select(selectedPers);

			selectedPers = ct.getNotifications().stream().filter(x -> {
				return x.getType() == 4;
			}).map(Notification::getPerson).toArray(Person[]::new);

			snmp.select(selectedPers);

			selectedPers = ct.getNotifications().stream().filter(x -> {
				return x.getType() == 5;
			}).map(Notification::getPerson).toArray(Person[]::new);

			snmp2.select(selectedPers);
		}

		UIUtils.setFullWidth(mail, snmp, snmp2);

		Window wnd = UIUtils.buildInputWindow(get("checktask.notification.editor.title")) //
				.withOkButtonCaption(get("txt.button.save")) //
				// .withValidator( ( results, binder, components ) -> {
				// if ( ( mail.getValue() == null || mail.getValue().isEmpty() )
				// && ( snmp.getValue() == null || snmp.getValue().isEmpty() )
				// && ( snmp2.getValue() == null || snmp2.getValue().isEmpty() ) ) {
				// results.add( ValidationResult.error( get( "txt.error.val.required" ) ) );
				// }
				// } ) //
				.withResultOk((src, bnd) -> {
					handleNotificationSave(src, bnd, ct, mail, snmp, snmp2);
				}) //
				.withCancelButtonCaption(get("txt.button.cancel")) //
				.withLayout(new VerticalLayout()) //
				.withComponents(mail, snmp, snmp2) //
				.build();

		wnd.center();
		wnd.setWidth(40.0f, Unit.PERCENTAGE);
		wnd.setHeight(700.0f, Unit.PIXELS);
		UIUtils.setFullWidth(wnd.getContent());
		UI.getCurrent().addWindow(wnd);

	}

	private void handleNotificationSave(Window src, Binder<Object> bnd, CheckTask ct, TwinColSelect<Person> mail,
			TwinColSelect<Person> snmp, TwinColSelect<Person> snmp2) {

		Ebean.beginTransaction();
		try {
			List<Notification> notes = new ArrayList<>();
			mail.getValue().forEach(p -> {
				Notification note = new Notification();
				note.setPerson(p);
				note.setType(1);
				notes.add(note);
			});
			snmp.getValue().forEach(p -> {
				Notification note = new Notification();
				note.setPerson(p);
				note.setType(4);
				notes.add(note);
			});
			snmp2.getValue().forEach(p -> {
				Notification note = new Notification();
				note.setPerson(p);
				note.setType(5);
				notes.add(note);
			});
			if (ct.getNotifications() != null && ct.getNotifications().size() > 0) {
				Ebean.deleteAll(ct.getNotifications());
			}
			ct.setNotifications(notes);
			Ebean.save(ct);
			Ebean.commitTransaction();

		} catch (Exception ex) {
			LOG.error("Fehler beim speichern: {}", ex.getLocalizedMessage());
			LOG.trace("", ex);
			UIUtils.showNotification(get("txt.error.general-form"), //$NON-NLS-1$
					Type.WARNING_MESSAGE);
		} finally {
			Ebean.endTransaction();
		}
		refresh();

	}

	/** Gibt ein Set von Pruefautraege zuruek, die aktiviert/deaktiviert wurden */
	private Set<CheckTask> activateTyp(Set<CheckTask> selected, Actions aktion) {

		if (aktion.getId() == 1) {

			selected.stream().forEach(c -> {
				c.setCheckday0Sun(false);
				c.setCheckday1Mon(true);
				c.setCheckday2Tue(true);
				c.setCheckday3Wed(true);
				c.setCheckday4Thu(true);
				c.setCheckday5Fri(true);
				c.setCheckday6Sat(false);
			});

		} else if (aktion.getId() == 2) {

			selected.stream().forEach(c -> {
				c.setCheckday0Sun(true);
				c.setCheckday0Sun(true);
				c.setCheckday1Mon(true);
				c.setCheckday2Tue(true);
				c.setCheckday3Wed(true);
				c.setCheckday4Thu(true);
				c.setCheckday5Fri(true);
				c.setCheckday6Sat(true);
			});
		} else if (aktion.getId() == 5) {

			selected.stream().forEach(c -> {
				c.setCheckday0Sun(false);
				c.setCheckday0Sun(false);
				c.setCheckday1Mon(false);
				c.setCheckday2Tue(false);
				c.setCheckday3Wed(false);
				c.setCheckday4Thu(false);
				c.setCheckday5Fri(false);
				c.setCheckday6Sat(false);
			});
		} else if (aktion.getId() == 3) {
			selected.stream().forEach(c -> {
				c.setTimeStart("6:00");
				c.setTimeEnd("17:00");
			});

		} else if (aktion.getId() == 4) {
			selected.stream().forEach(c -> {
				c.setTimeStart("5:00");
				c.setTimeEnd("21:00");
			});

		}
		return selected;
	}

	/**
	 * Gibt ein false zuruek, falls alle Pruefungen des OAF leer sind: Validiert
	 * die als Parameter eingegangenen Felder
	 */
	private boolean validate(TextField collectionidAvailable, TextField collectionidContent, TextField featureid) {
		boolean mess = true;
		if (collectionidAvailable.isEmpty() && collectionidContent.isEmpty() && featureid.isEmpty()) {
			mess = false;
		}
		return mess;
	}

	/**
	 * Validates the input string. Returns false if the string does not meet certain
	 * conditions.
	 * <p>
	 * The method checks for the following conditions: - If the string is empty, it
	 * returns true. - If the string contains "|", it splits the string by "|" and
	 * validates each substring. - For each substring (or the original string if it
	 * doesn't contain "|"): - If it doesn't contain "}", it returns false. - If it
	 * contains "}" and the substring before "}" is empty or contains only spaces,
	 * it returns false. - If it contains "}" and the substring before "}" is not
	 * empty and doesn't contain only spaces, and there's no substring after "}" or
	 * the substring after "}" is empty, it returns false.
	 * 
	 * @param checkParameter The input string to validate.
	 * @return true if the string meets all the conditions, false otherwise.
	 */
	private boolean validate(String checkParameter) {
		boolean mess = true;
		if (!checkParameter.isEmpty()) {
			String[] featureArray = (checkParameter.contains("|")) ? checkParameter.split("\\|")
					: new String[] { checkParameter };
			for (String fture : featureArray) {
				String[] splitFture = fture.split("\\}");
				if (  fture.isEmpty() || //
	                     fture.indexOf( "}" ) < 0 || //
	                     splitFture[0].trim().length() == 1 || //
	                     splitFture[1].trim().isEmpty()) {
					mess = false;
				}
			}
			return mess;
		}
		return mess;
	}

}
