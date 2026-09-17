package com.library.service;

import com.library.model.Member;
import com.library.storage.FileStorage;
import com.library.util.LibraryException;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Module 1: Member Management.
 * Handles registration, updates, search and fine payment for library members.
 */
public class MemberService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final FileStorage storage = new FileStorage("members.csv");
    private final Map<String, Member> members = new LinkedHashMap<>();

    public MemberService() {
        for (Member m : storage.readAll(Member::fromCsv)) {
            members.put(m.getMemberId(), m);
        }
    }

    public void addMember(Member member) throws LibraryException {
        if (members.containsKey(member.getMemberId())) {
            throw new LibraryException("Member ID " + member.getMemberId() + " already exists.");
        }
        if (!EMAIL_PATTERN.matcher(member.getEmail()).matches()) {
            throw new LibraryException("Invalid email format: " + member.getEmail());
        }
        members.put(member.getMemberId(), member);
        persist();
    }

    public void updateMember(String memberId, String name, String email, String phone)
            throws LibraryException {
        Member m = getMemberOrThrow(memberId);
        if (name != null && !name.isBlank()) m.setName(name);
        if (email != null && !email.isBlank()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new LibraryException("Invalid email format: " + email);
            }
            m.setEmail(email);
        }
        if (phone != null && !phone.isBlank()) m.setPhone(phone);
        persist();
    }

    public void removeMember(String memberId) throws LibraryException {
        Member m = getMemberOrThrow(memberId);
        if (m.getOutstandingFine() > 0) {
            throw new LibraryException("Cannot remove member with outstanding fine of Rs." + m.getOutstandingFine());
        }
        members.remove(memberId);
        persist();
    }

    public Member getMemberOrThrow(String memberId) throws LibraryException {
        Member m = members.get(memberId);
        if (m == null) throw new LibraryException("No member found with ID: " + memberId);
        return m;
    }

    public List<Member> searchByName(String keyword) {
        String k = keyword.toLowerCase();
        List<Member> result = new ArrayList<>();
        for (Member m : members.values()) {
            if (m.getName().toLowerCase().contains(k)) result.add(m);
        }
        return result;
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    public void payFine(String memberId, double amount) throws LibraryException {
        Member m = getMemberOrThrow(memberId);
        if (!m.payFine(amount)) {
            throw new LibraryException("Invalid payment amount. Outstanding fine is Rs." + m.getOutstandingFine());
        }
        persist();
    }

    public void persist() {
        storage.writeAll(new ArrayList<>(members.values()), Member::toCsv);
    }
}
