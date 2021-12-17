/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.frontend.pages;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byClassName;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import com.codeborne.selenide.ElementsCollection;

public class AssetNotesPage {

    private ElementsCollection notes = $$(byClassName("note"));

    protected AssetNotesPage() {
        $(byText("Notes")).click();
    }

    public void createNote(String note) {
        $(byAttribute("formcontrolname", "note")).setValue(note);
        $(byId("notes-form--save")).click();
    }

    public void assertNumberOfNotes(int expectedSize) {
        notes.shouldHave(size(expectedSize));
    }

    public void assertNoteAtPosition(int index, String note) {
        notes.get(index)
            .$(byClassName("note-text"))
            .shouldHave(text("note"));
    }
}
