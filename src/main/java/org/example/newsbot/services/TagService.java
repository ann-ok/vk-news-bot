package org.example.newsbot.services;

import org.example.newsbot.dao.TagDao;
import org.example.newsbot.models.Tag;

import java.util.List;

public class TagService {
    private TagDao tagDao = new TagDao();

    public TagService() {
    }

    public Tag getTag(int id) {
        return tagDao.findById(id);
    }

    public Tag findTag(String name) {
        return tagDao.findByName(name, true);
    }

    public Tag findTagInsensitive(String name) {
        return tagDao.findByName(name, false);
    }

    public void saveTag(Tag tag) {
        tagDao.save(tag);
    }

    public void deleteTag(Tag tag) {
        tagDao.delete(tag);
    }

    public void updateTag(Tag tag) {
        tagDao.update(tag);
    }

    public List<Tag> findAllTags() {
        return tagDao.findAll();
    }
}
