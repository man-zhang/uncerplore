package no.simula.se.uncertainty.evolution.domain;

public enum OccurOfIndS {
	NoneInds, //O0 none of indeterminacy source occurred
	SpecifiedInds, //O1 only specified of indeterminacy source occurred
	OtherInds, //O2 specified of indeterminacy did not occurred and at least one of other indeterminacy source occurred.
	Combined, //O3 specified of indeterminacy source and at least one of other indeterminacy source occurred

}
