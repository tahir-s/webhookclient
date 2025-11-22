package com.attribe.webhookclient.pojo.whatsapp;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Entry {

	private String id;
    private List<Change> changes;
}
